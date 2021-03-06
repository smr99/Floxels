/*
 *  FloxelsStory.java
 *  Copyright (c) 2016 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.floxels;

import java.util.*;

// controlling class for the game
public class FloxelsStory extends Story {
  
  // story event: close the game
  public static class EventExitGame extends StoryEvent {
  } // FloxelsStory.EventExitGame

  // description of the majority population
  static private final String kDescriptions[] 
      = { "Evil", "Treacherous", "Brutal", "Decadent", "Tyrannical",
          "Heretical", "Vile", "Barbaric", "Sinful", "Despotic",
          "Godless", "Foul", "Wicked", "Immoral", "Corrupt", 
          "Loathsome", "Lawless", "Pompous", "Impotent", "Despicable",
          "Foolish", "Crooked", "Vain", "Villainous", "Zealous" };
  
  // enumeration of population types
  static private final int kMajorityType = 0,
                           kMinorityType = 1;
  static private final int kNumTypes     = 2;

  // how many floxels of each type we're dealing with
  static private final int kMajorityPopulationMax = 900,
                           kMinorityPopulation    = 100;

  // factors controlling majority floxels' instincts for hunting
  static private final float kHuntStrength      = 10.0f,
                             kHuntStrengthBoost = 8.0f,
                             kHuntStrengthFail  = -0.3f,
                             kHuntFailureFrac   = 0.7f;
  
  // factors controlling minority floxels' instincts for fleeing
  static private final float kFleeStrength         = 10.0f,
                             kFleeStrengthReverse  = -5.0f,
                             kFleeReverseFrac      = 0.7f,
                             kFleeReverseSoundFrac = 0.77f,
                             kFleeStrengthResign   = 0.5f;
  static private final int   kFleeResignNum        = 10;

  // seconds until various events occur
  static private final float kIntroDelay          = 0.5f,
                             kRestartDelay        = 2.0f,
                             kRestartInitialDelay = 0.5f,
                             kSpawnDelay          = 0.15f,
                             kSpawnDelayFirst     = 1.1f;
  
  // references to some specific objects
  private Background   mBackground;
  private Flow         mFlows[];
  private Floxels      mFloxels;
  private Maze         mMaze;
  private VentControl  mVentControls[];
  private Cursor       mCursor;
  private Score        mScore;
  private FrameRate    mFrameRate;
  private ColourScheme mColourScheme;
  
  // player's current level (0, 1, 2, ...)
  private int mLevel;
  
  // number of enemy floxels at the start of the level (not including converts)
  private int mMajorityPopulation;
  
  // tweak to the aggression of the majority floxels (1.0 for no change)
  private float mDifficultyFactor;

  // seconds until the first floxels appear (or zero)
  private float mIntroTimer;

  // seconds until new floxels can be introduced (or zero)
  private float mRestartTimer;

  // seconds until the instruction text changes (or zero)
  private float mInstructionTimer;

  // which line of instructions to display next
  private int mInstructionIndex;
  
  // true if the minority is suddenly on the offensive
  private boolean mReversed;
  
  // check whether the 'back' button has been pressed
  private boolean mQuitTriggered;
  
  // constructor
  public FloxelsStory() {

    mLevel = -1;

  } // constructor

  // advance the Story by one frame
  public Story advance(LinkedList<StoryEvent> storyEvents,
                       SpriteManager          spriteManager) {

    Story newStory = null;
    
    // process the story event list
    for ( Iterator<StoryEvent> it = storyEvents.iterator() ; it.hasNext() ; ) {
      StoryEvent event = it.next();

      if ( event instanceof Story.EventGameBegins ) {
        mLevel = 0;
        prepareNewStory(spriteManager);
        it.remove();
      } // Story.EventGameBegins

      if ( event instanceof FloxelsStory.EventExitGame ) {
        Env.exit();
        it.remove();
      } // FloxelsStory.EventExitGame

      if ( event instanceof LaunchCursor.EventComplete ) {
        mRestartTimer = kRestartInitialDelay;
        fadeText(spriteManager);
        addInstructions(spriteManager, Maze.changeTime());
        mMaze.changeToNext();
        it.remove();
      } // LaunchCursor.EventComplete
      
      if ( event instanceof Cursor.EventFloxelsSummoned ) {
        fadeText(spriteManager);
        it.remove();
      } // Cursor.EventFloxelsSummoned
      
      if ( event instanceof Floxels.EventPopulationDestroyed ) {
        if ( mFloxels.numFloxels(kMajorityType) == 0 ) {
          newLevel(spriteManager);
        } else if ( mFloxels.numFloxels(kMinorityType) 
                    + mCursor.numCaptured() == 0 ) {
          restartLevel(spriteManager);
        }
        it.remove();
      } // Floxels.EventPopulationDestroyed

      if ( event instanceof Maze.EventMazeChanged ) {
        for ( Flow flow : mFlows ) prepareFlow(flow, mMaze.data());
        it.remove();
      } // Maze.EventMazeChanged
      
    } // for each story event

    updateHuntFactors();
    updateFlows(spriteManager);
    
    // pause before adding the player's cursor
    if ( mRestartTimer > 0.0f && !mMaze.changing() ) {
      mRestartTimer -= Env.TICK_TIME;
      if ( mRestartTimer <= 0.0f ) {
        assert( mCursor == null );
        mCursor = new Cursor(kMinorityPopulation, kMinorityType, mFloxels);
        spriteManager.addSprite(mCursor);
        mRestartTimer = 0.0f;
      }
    }

    // unleash floxels at the start of the game
    if ( mIntroTimer > 0.0f ) {
      mIntroTimer -= Env.TICK_TIME;
      assert( mFloxels.numFloxels(kMajorityType) == 0 );
      assert( mFloxels.numFloxels(kMinorityType) == 0 );
      assert( mCursor == null );
      if ( mIntroTimer <= 0.0f ) {
        int total = mMajorityPopulation + kMinorityPopulation;
        spriteManager.addSprite(new LaunchCursor(total, 
                                                 kMajorityType, mFloxels));
        mIntroTimer = 0.0f;
      }
    }

    // sanity check
    if ( mIntroTimer == 0 &&
         spriteManager.findSpriteOfType(LaunchCursor.class) == null &&
         spriteManager.findSpriteOfType(Spawner.class) == null ) {
      int total = mFloxels.numFloxels(kMajorityType)
                + mFloxels.numFloxels(kMinorityType)
                + ( mCursor != null ? mCursor.numCaptured() : 0 );
      assert( total == mMajorityPopulation + kMinorityPopulation );
    }

    // update the score
    if ( mCursor != null ) {
      int num = mCursor.numCaptured();
      if ( !mCursor.summoning() ) num += mFloxels.numFloxels(kMinorityType);
      mScore.set(num);
    }

    // occasionally vary the instruction text
    if ( mInstructionTimer > 0 ) {
      changeInstructions(spriteManager);
    }
    
    // check the 'back' button
    if ( Env.quitButton() ) {
      if ( !mQuitTriggered ) {
        if ( spriteManager.findSpriteOfType(TitleImage.class) != null ) {
          Env.exit();
        } else {
          newStory = new QuitStory(this);
          storyEvents.add(new Story.EventStoryBegins());
        }
      }
      mQuitTriggered = true;
    } else {
      mQuitTriggered = false;
    }
    
    // change of story (usually null)
    return newStory;

  } // advance()

  // balance the aggressive tendencies of the floxels based on their numbers
  private void updateHuntFactors() {
    
    final int majNum = mFloxels.numFloxels(kMajorityType),
              minNum = mFloxels.numFloxels(kMinorityType),
              capNum = (mCursor != null ? mCursor.numCaptured() : 0);
    
    final int total  = majNum + minNum;
    if ( total == 0 ) return;
    
    final float minFrac    = minNum/(float)total,
                minFracInc = (minNum+capNum)/(float)(total+capNum);
    
    // how tasty the minority population looks
    float huntStrength = kHuntStrength * mDifficultyFactor;
    if ( minNum == 0 ) {
      huntStrength = 0.0f;
    } else if ( minNum > 0 && minNum < kMinorityPopulation ) {
      final float h = minNum/(float)kMinorityPopulation;
      assert( h >= 0.0f && h <= 1.0f );
      final float boost = (1-h)*(1-h);
      huntStrength += boost*kHuntStrengthBoost*kHuntStrength;
    } else if ( minFrac > kHuntFailureFrac ) {
      final float h = (minFrac - kHuntFailureFrac)/(1.0f - kHuntFailureFrac);
      assert( h >= 0.0f && h <= 1.0f );
      huntStrength *= (1-h) + h*kHuntStrengthFail;
    }
    mFloxels.setHuntingStrength(kMinorityType, -huntStrength);
    
    // how scary the majority population looks
    float fleeStrength = kFleeStrength; 
    if ( minFrac > kFleeReverseFrac ) {
      final float h = (minFrac - kFleeReverseFrac)/(1.0f - kFleeReverseFrac);
      assert( h >= 0.0f && h <= 1.0f );
      fleeStrength *= (1-h) + h*kFleeStrengthReverse;
    } else if ( minNum < kFleeResignNum ) {
      final float h = (minNum-1)/(float)(kFleeResignNum-1);
      fleeStrength *= h + (1-h)*kFleeStrengthResign;
    }
    mFloxels.setHuntingStrength(kMajorityType, +fleeStrength);

    // sound effect
    if ( minFrac > kFleeReverseSoundFrac && !mReversed ) {
      Env.sounds().play(Sounds.REVERSAL);
      mReversed = true;
    } else if ( minFracInc < 0.75f*kFleeReverseSoundFrac ) {
      mReversed = false;
    }
    
    // vents influence large-scale movement
    final float v = Math.max(0.0f, Math.min(1.0f, (minFrac - 0.6f)/0.1f));
    mVentControls[kMajorityType].setHuntStrength(1.0f - v);
    mVentControls[kMinorityType].setHuntStrength(v);

    mVentControls[kMajorityType].setNoMercy( minNum < kMinorityPopulation/3 );
    mVentControls[kMinorityType].setNoMercy( majNum < kMinorityPopulation/2 );
    
  } // updateHuntFactors()
  
  // bring the flow fields up-to-date 
  private void updateFlows(SpriteManager spriteManager) {

    mFloxels.defineFlockingSources();
    mFloxels.addHuntingSources();

    for ( Sprite s : spriteManager.list() ) {
      if ( s instanceof SourceTerm ) {
        SourceTerm st = (SourceTerm)s;
        for ( int type = 0 ; type < kNumTypes ; type++ ) {
          Flow flow = mFlows[type];
          st.addToSource(type, flow.source(), flow.refineFactor());
        }
      }
    }

    for ( VentControl v : mVentControls ) v.advance();
    
    for ( Flow flow : mFlows ) flow.solve();
    
  } // updateFlows()

  // tweak the difficulty for the level
  private void setLevelDifficulty() {

    mMajorityPopulation = 0;
    for ( int level = 0 ; level <= mLevel ; level++ ) {
      if      ( mMajorityPopulation ==  0 ) mMajorityPopulation =  400;
      else if ( mMajorityPopulation < 500 ) mMajorityPopulation += 25;
      else if ( mMajorityPopulation < 700 ) mMajorityPopulation += 20;
      else                                  mMajorityPopulation += 10;
    }
    
    final float minFactor = 0.4f;
    if ( mMajorityPopulation <= kMajorityPopulationMax ) {
      final int majPopStart = 200;
      final float h = (mMajorityPopulation - majPopStart)
                      /(float)(kMajorityPopulationMax - majPopStart);
      assert( h >= 0.0f && h <= 1.0f );
      mDifficultyFactor = (1-h) + h*minFactor;
    } else {
      final float h = (mMajorityPopulation-kMajorityPopulationMax)/10.0f;
      assert( h > 0.0f );
      mDifficultyFactor = minFactor + 0.005f*h;
    }

    mMajorityPopulation = Math.min(mMajorityPopulation, 
                                   kMajorityPopulationMax);
    
    if ( Env.debugMode() ) {
      Env.debug("Level " + mLevel 
                + " (" + mMajorityPopulation
                + " / " + mDifficultyFactor + ")");
    }
    
  } // setLevelDifficulty()
  
  // things to do when the game begins
  private void prepareNewStory(SpriteManager spriteManager) {

    prepareNewSprites(spriteManager);

    setLevelDifficulty();
    setFloxelColours();
    
    mCursor = null;
    
    mIntroTimer = kIntroDelay;
    mRestartTimer = 0.0f;
    mInstructionTimer = 0.0f;
    mInstructionIndex = 0;
    
    mReversed = false;
    
  } // prepareNewStory()

  // initialization of sprites, etc.
  private void prepareNewSprites(SpriteManager spriteManager) {

    mBackground = new Background();
    spriteManager.addSprite(mBackground);

    mMaze = new Maze();
    spriteManager.addSprite(mMaze);

    mScore = new Score();
    spriteManager.addSprite(mScore);
    
    mFrameRate = new FrameRate();
    spriteManager.addSprite(mFrameRate);
    
    mFlows = new Flow[kNumTypes];
    for ( int k = 0 ; k < mFlows.length ; k++ ) {
      mFlows[k] = new Flow(Env.numTilesX(), Env.numTilesY(), 4);
      prepareFlow(mFlows[k], mMaze.data());
      mFlows[k].reset();
      mFlows[k].solve();
    }
    
    mFloxels = new Floxels(mFlows);
    spriteManager.addSprite(mFloxels);

    for ( int type = 0 ; type < kNumTypes ; type++ ) {
      mFloxels.setHuntingStrength(type, 0.0f);
    }
    
    mVentControls = new VentControl[kNumTypes];
    for ( int type = 0 ; type < kNumTypes ; type++ ) {
      mVentControls[type] = new VentControl(mFlows[type], mFloxels, type);
    }
    mVentControls[kMajorityType].setHuntStrength(1.0f);
    mVentControls[kMinorityType].setHuntStrength(0.0f);

    mColourScheme = new ColourScheme();

    spriteManager.addSprite(new TitleImage());
    spriteManager.addSprite(new TextObject((Env.touchScreen()?"Touch":"Click")
                                           + " to Begin", 
                                           2.8f, false, 0.0f));
    
  } // prepareNewSprites()
  
  // build a flow consistent with the maze
  private void prepareFlow(Flow flow, MazeData maze) {

    final int nx = Env.numTilesX(),
              ny = Env.numTilesY();
    
    final float inFlow = VentControl.inFlowDefault(); 

    float flowWalls[][][] = flow.walls();
    assert( flowWalls.length == ny && flowWalls[0].length == nx );

    for ( int iy = 0 ; iy < ny ; iy++ ) {
      for ( int ix = 0 ; ix < nx ; ix++ ) {
        float walls[] = flowWalls[iy][ix];
        walls[Env.NORTH] = ( maze.horizWall(ix, iy)   ? inFlow : Flow.OPEN );
        walls[Env.SOUTH] = ( maze.horizWall(ix, iy+1) ? inFlow : Flow.OPEN );
        walls[Env.WEST]  = ( maze.vertWall(ix, iy)    ? inFlow : Flow.OPEN );
        walls[Env.EAST]  = ( maze.vertWall(ix+1, iy)  ? inFlow : Flow.OPEN );
      }
    }
    
  } // prepareFlow()
  
  // reset the floxels to replay the level
  private void restartLevel(SpriteManager spriteManager) {

    mScore.set(0);
    
    mCursor.cancel();
    spriteManager.removeSprite(mCursor);
    mCursor = null;

    mRestartTimer = kRestartDelay;
    addInstructions(spriteManager, kRestartDelay-0.6f);

    mVentControls[kMajorityType].setHuntStrength(1.0f);
    mVentControls[kMinorityType].setHuntStrength(0.0f);

    Env.sounds().play(Sounds.FAIL, 15);
    
  } // restartLevel()

  // introduce a new population for the next level
  private void newLevel(SpriteManager spriteManager) {

    mCursor.cancel();
    spriteManager.removeSprite(mCursor);
    mCursor = null;
    
    assert( mFloxels.numFloxels(kMajorityType) == 0 );
    assert( mFloxels.numFloxels(kMinorityType)  
                      == mMajorityPopulation + kMinorityPopulation );
    mScore.set( mFloxels.numFloxels(kMinorityType) );
    mScore.bank();

    mLevel += 1;
    setLevelDifficulty();

    switchPopulations(spriteManager);
    
    mRestartTimer = kRestartInitialDelay;
    addInstructions(spriteManager, Maze.changeTime());
    
    growPopulation(spriteManager);
    
    mMaze.changeToNext();

    Env.sounds().play(Sounds.SUCCESS, 15);
    
  } // newLevel() 

  // majority and minority floxel populations switch places
  private void switchPopulations(SpriteManager spriteManager) {
    
    mFloxels.switchFloxelTypes();
    setFloxelColours();
    
    for ( int type = 0 ; type < kNumTypes ; type++ ) {
      mVentControls[type].switchFloxelTypes();
    }

    VentControl temp = mVentControls[0];
    mVentControls[0] = mVentControls[1];
    mVentControls[1] = temp;

  } // switchPopulations()

  // define the colours for the two populations
  private void setFloxelColours() {
    
    mColourScheme.advance();
    mFloxels.setFloxelColour(kMajorityType, mColourScheme.oldIndex());
    mFloxels.setFloxelColour(kMinorityType, mColourScheme.newIndex());
    
  } // setFloxelColours()
  
  // add to the majority population while it is below its target 
  private void growPopulation(SpriteManager spriteManager) {
    
    assert( spriteManager.findSpriteOfType(Spawner.class) == null );
    
    int delta = (mMajorityPopulation + kMinorityPopulation) 
                - mFloxels.numFloxels(kMajorityType);
    float delay = kSpawnDelayFirst;
    
    int count[][] = mFloxels.countFloxels(kMajorityType);
    
    while ( delta > 0 ) {
    
      int num = ( delta >= 10 ) ? 5
              : ( delta > 6 )   ? (delta/2)
                                : delta;
      
      int ix=0, iy=0;
      for ( int k = 0 ; k < 8 ; k++ ) {
        int kx = Env.randomInt( Env.numTilesX() );
        int ky = Env.randomInt( Env.numTilesY() );
        if ( k == 0 || count[ky][kx] < count[iy][ix] ) {
          ix = kx;
          iy = ky;
        }
        if ( count[iy][ix] == 0 ) break;
      }
      float x = ix + Env.randomFloat(0.1f, 0.9f),
            y = iy + Env.randomFloat(0.1f, 0.9f);

      spriteManager.addSprite( new Spawner(mFloxels, x, y, 
                                           kMajorityType, num,
                                           delay) );
      delta -= num;
      delay += kSpawnDelay;
      
    }
    
  } // growPopulation()
  
  // remove any text or title objects
  private void fadeText(SpriteManager spriteManager) {

    for ( Sprite sp : spriteManager.list() ) {
      if ( sp instanceof TitleImage ) {
        ((TitleImage)sp).fade(); 
      } else if ( sp instanceof TextObject ) {
        ((TextObject)sp).fade(); 
      }
    }
    
    mInstructionTimer = 0.0f;
    
  } // fadeText()

  // display some text
  private void addInstructions(SpriteManager spriteManager, float delay) {
    
    float y0 = 0.5f*Env.numTilesY(),
          dy = 0.75f;
    
    String name = ColourScheme.name( mColourScheme.oldIndex() );
    String description = kDescriptions[ mLevel % kDescriptions.length ];
    
    spriteManager.addSprite(new TextObject("Overthrow the " + description 
                                           + " " + name, 
                                           y0+dy, true, delay));
    
    mInstructionTimer = 7.5f;
    mInstructionIndex = 0;
    
    spriteManager.addSprite(new TextObject(nextInstructionText(), 
                                           y0-dy, true, delay));
    
  } // addInstructions()
  
  // get the next line of instruction text
  private String nextInstructionText() {
    
    String text = null;
    if ( mInstructionIndex == 0 ) {
      text = (Env.touchScreen() ? "Press" : "Click") 
             + " to call Floxels into your bubble";
      mInstructionIndex += 1;
    } else if ( mInstructionIndex == 1 ) {
      text = "Drag bubble to make Floxels chase after it";
      mInstructionIndex += 1;
    } else if ( mInstructionIndex == 2 ) {
      text = "Floxels in big groups are bright and strong";
      mInstructionIndex += 1;
    } else if ( mInstructionIndex == 3 ) {
      text = "Small groups are dark and weak";
      mInstructionIndex += 1;
    } else {
      text = "Strong Floxels convert weaker ones";
      mInstructionIndex = 0;
    }
    return text;
    
  } // instructionText()
  
  // modify the instruction text
  private void changeInstructions(SpriteManager spriteManager) {

    if ( mInstructionTimer == 0.0f ) return;
    
    final float y = 0.5f*Env.numTilesY() - 0.75f;

    TextObject text = null;
    for ( Sprite sp : spriteManager.list() ) {
      if ( sp instanceof TextObject ) {
        TextObject t = (TextObject)sp;
        if ( y > t.yMin() && y < t.yMax() ) text = t;
      }
    }
    if ( text == null || text.fading() ) {
      mInstructionTimer = 0.0f;
      return;
    }
    
    mInstructionTimer -= Env.TICK_TIME;
    if ( mInstructionTimer <= 0.0f ) {
      text.fade();
      spriteManager.addSprite(new TextObject(nextInstructionText(), 
                                             y, true, 0.2f));
      mInstructionTimer = 6.0f;
    }
    
  } // updateInstructions()
  
} // class FloxelsStory
