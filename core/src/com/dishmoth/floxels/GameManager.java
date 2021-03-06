/*
 *  GameManager.java
 *  Copyright (c) 2016 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.floxels;

import java.util.*;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

// class that orchestrates the behaviour of the game
// basically comprises a Story object and a list of Sprite objects
public class GameManager {

  // the current story controller
  // different Story objects will be used during the coarse of a game 
  private Story mStory = null;
  
  // the collection of Sprite objects currently active
  // Sprites may come and go but the SpriteManager remains throughout
  private SpriteManager mSpriteManager = null;

  // list of story events
  // generated by the sprites to be processed by the current story
  private LinkedList<StoryEvent> mStoryEvents = null;
  
  // constructor
  public GameManager(Story startingStory) {
    
    mStory         = startingStory;
    mSpriteManager = new SpriteManager();

    mStoryEvents = new LinkedList<StoryEvent>();
    mStoryEvents.add(new Story.EventGameBegins());
    
  } // constructor
  
  // advance the game (story and sprites) by one frame
  public void advance() {

    // advance the story (possibly replace with a new story; advance that too)
    while (true) {
      Story changeOfStory = mStory.advance(mStoryEvents, mSpriteManager);
      if ( changeOfStory == null ) break;
      mStory = changeOfStory;
    }
    
    // advance the sprites
    mSpriteManager.advance(mStoryEvents);
    
    // play queued sounds
    Env.sounds().advance();
    
  } // advance()

  // draw the current game screen
  // (for camera tricks a screen buffer is used)
  public void draw(SpriteBatch batch) {

    mSpriteManager.draw(batch);
    
  } // draw()
  
} // class GameManager
