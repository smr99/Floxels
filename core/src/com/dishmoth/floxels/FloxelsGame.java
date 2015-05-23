/*
 *  FloxelsGame.java
 *  Copyright Simon Hern 2014
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.floxels;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

// main application class
public class FloxelsGame implements ApplicationListener {

  // camera controlling screen coordinates
  private OrthographicCamera mCamera;

  // object for drawing sprites
  private SpriteBatch mSpriteBatch;
  
  // the main game controller
  private GameManager mGameManager;

  // seconds since the last update
  private double mTimeSince;

  // debug timing measurements
  private TimingStats mTimingStats;
  
  // Called when the application is first created.
	public void create() {

	  Env.debug("create()");
	  
    Env.initialize();
    //Env.spriteStore().prepare();
    Env.sounds().initialize();

    int width = Gdx.graphics.getWidth();
    int height = Gdx.graphics.getHeight();

    mCamera = new OrthographicCamera(width, height);
    mCamera.translate(width/2.0f, height/2.0f);
    mCamera.update();
    
    mSpriteBatch = new SpriteBatch();
    mSpriteBatch.setProjectionMatrix(mCamera.combined);

    int tile = Math.min(width/Env.numTilesX(), height/Env.numTilesY());
    Env.setTileWidth(tile);
    
    mGameManager = new GameManager(new FloxelsStory());

    mTimeSince = 0.0f;

    mTimingStats = new TimingStats();
    
	} // ApplicationListener.create()

  // Called when the application is resized. This can happen at any point
  // during a non-paused state but will never happen before a call to create().
  public void resize(int width, int height) {

    Env.debug("resize(" + width + "," + height + ")");

  } // ApplicationListener.resize()

  // Called when the application is paused, usually when it's not active or 
  // visible on screen.  An application is also paused before it is destroyed.
  public void pause () {
    
    Env.debug("pause()");
    
  } // ApplicationListener.pause()

  // Called when the application is resumed from a paused state, 
  // usually when it regains focus.
  public void resume () {
    
    Env.debug("resume()");
    
  } // ApplicationListener.resume()

  // Called when the application is destroyed. Preceded by a call to pause().
  public void dispose () {
    
    Env.debug("dispose()");
    //Env.spriteStore().dispose();
    
  } // ApplicationListener.dispose()

  // Called when the application should render itself.
  public void render() {
    
    final float deltaTime = Gdx.graphics.getRawDeltaTime();
    mTimingStats.update(deltaTime);
    
    final float tick = Env.TICK_TIME;
    final float maxStep = 0.1f;
    
    mTimeSince += Math.min(deltaTime, maxStep);
    while ( mTimeSince > tick ) {
      update();
      mTimeSince -= tick;
    }
    
    draw();
        
  } // ApplicationListener.render()

  // update the game logic
  private void update() {

    mGameManager.advance();
    
  } // update()

  // display the game screen
  private void draw() {

    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    
    mSpriteBatch.begin();
    mGameManager.draw(mSpriteBatch);
    mSpriteBatch.end();
    
  } // draw()

} // class FloxelsGame
