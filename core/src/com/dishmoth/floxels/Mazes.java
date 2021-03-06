/*
 *  Mazes.java
 *  Copyright (c) 2016 Simon Hern
 *  Contact: dishmoth@yahoo.co.uk, dishmoth.com, github.com/dishmoth
 */

package com.dishmoth.floxels;

// collection of maze data
public class Mazes {

  static private final String kTitleData10x10[] =
        { " _ _ _ _ _ _ _ _ _ _ ",
          "I  _ _ _  I  _ _ _  I",
          "I I  _ _ _I_ _ _  I I",
          "I_                 _I",
          "I                   I",
          "I I               I I",
          "I I               I I",
          "I_                 _I",
          "I    _ _ _ _ _ _    I",
          "I I_ _ _  I  _ _ _I I",
          "I_ _ _ _ _I_ _ _ _ _I" };

  static private final String kTitleData11x9[] =
        { " _ _ _ _ _ _ _ _ _ ",
          "I  _ _       _ _  I",
          "I I  _ _I I_ _  I I",
          "I_ _           _ _I",
          "I                 I",
          "I I             I I",
          "I I             I I",
          "I I             I I",
          "I_ _           _ _I",
          "I    _ _   _ _    I",
          "I I_ _  I I  _ _I I",
          "I_ _ _ _ _ _ _ _ _I" };

  static private final String kTitleData12x8[] =
        { " _ _ _ _ _ _ _ _ ",
          "I  _ _  I  _ _  I",
          "I I  _ _I_ _  I I",
          "I_ _         _ _I",
          "I               I",
          "I I           I I",
          "I I           I I",
          "I I           I I",
          "I I           I I",
          "I_ _         _ _I",
          "I    _ _ _ _    I",
          "I I_ _  I  _ _I I",
          "I_ _ _ _I_ _ _ _I" };
  
  static private final String kMazeData10x10[][] = {

        // maze 1
        { " _ _ _ _ _ _ _ _ _ _ ",
          "I  _ _ _    I  _    I",
          "I   I  _ _I_ _ _ _I I",
          "I I I  _  I  _    I I",
          "I I I I  _I_  I I I I",
          "I  _I I I  _ _ _I  _I",
          "I I    _I I  _  I_  I",
          "I_  I_ _ _I   I I   I",
          "I    _ _  I I I I I I",
          "I I_ _    I I I I_  I",
          "I_ _ _ _I_ _ _I_ _ _I" },

        // maze 2
        { " _ _ _ _ _ _ _ _ _ _ ",
          "I  _ _  I  _ _ _ _  I",
          "I I  _  I I     I  _I",
          "I I   I_ _I I I_ _  I",
          "I  _I_ _ _ _I I    _I",
          "I_   _ _ _   _I I   I",
          "I  _ _  I   I  _ _I I",
          "I I  _ _I I I I  _  I",
          "I I_ _   _I I I_  I I",
          "I  _ _ _I   I_ _ _I I",
          "I_ _ _ _ _I_ _ _ _ _I" },
          
        // maze 3
        { " _ _ _ _ _ _ _ _ _ _ ",
          "I   I    _ _  I  _  I",
          "I I_  I_ _ _ _ _  I I",
          "I  _  I  _ _    I I I",
          "I   I  _I   I I_ _I I",
          "I I I I   I I   I  _I",
          "I I I_ _I I I I_ _  I",
          "I_  I  _ _I I_ _  I I",
          "I    _I   I_ _  I  _I",
          "I I_ _ _I    _ _I_  I",
          "I_ _ _ _ _I_ _ _ _ _I" },

        // maze 4
        { " _ _ _ _ _ _ _ _ _ _ ",
          "I  _ _    I  _ _ _  I",
          "I I  _ _I I I   I   I",
          "I I I  _ _ _ _I I I I",
          "I  _I I   I  _ _I  _I",
          "I_  I  _I  _ _   _  I",
          "I  _I   I_ _  I   I I",
          "I   I I  _ _  I I I I",
          "I I I I I   I_ _I I I",
          "I I_ _I   I_ _ _ _I I",
          "I_ _ _ _I_ _ _ _ _ _I" },

        // maze 5
        { " _ _ _ _ _ _ _ _ _ _ ",
          "I  _ _ _   _ _  I   I",
          "I   I   I I  _ _ _I I",
          "I I I I   I I  _  I I",
          "I I_  I I I_ _  I  _I",
          "I I  _I I_ _  I I_  I",
          "I I I  _ _ _  I_  I I",
          "I_  I_ _ _  I  _ _I I",
          "I  _ _ _ _ _I I   I I",
          "I I  _ _    I I I I I",
          "I_ _ _ _ _I_ _ _I_ _I" },
        
        };

  static private final String kMazeData11x9[][] = {

        // maze 1
        { " _ _ _ _ _ _ _ _ _ ",
          "I  _ _    I  _    I",
          "I   I  _I_ _ _ _I I",
          "I I I   I  _    I I",
          "I I I I I_  I I I I",
          "I  _I I  _ _ _I  _I",
          "I I  _ _I  _  I_  I",
          "I I I   I_  I I   I",
          "I_  I I I   I I I I",
          "I   I I I I I I I I",
          "I I_ _I   I I I_  I",
          "I_ _ _ _I_ _I_ _ _I" },
        
        // maze 2
        { " _ _ _ _ _ _ _ _ _ ",
          "I  _ _  I  _ _ _  I",
          "I I  _  I I     I I",
          "I I   I_ _I I I_  I",
          "I  _I_ _ _ _I I   I",
          "I_   _ _ _   _I I I",
          "I  _ _  I   I    _I",
          "I I  _ _I I I I   I",
          "I I_   _  I I I I I",
          "I_  I I  _I I  _I I",
          "I  _I_ _I   I_ _  I",
          "I_ _ _ _ _I_ _ _ _I" },

        // maze 3
        { " _ _ _ _ _ _ _ _ _ ",
          "I  _ _ _   _ _    I",
          "I I  _  I I  _ _I I",
          "I I_  I  _I I  _ _I",
          "I_  I I   I I_ _  I",
          "I  _I I I I_ _  I I",
          "I I  _I I_ _  I I I",
          "I I I  _ _  I I_  I",
          "I_  I_ _  I  _I   I",
          "I  _ _ _ _I_   _I I",
          "I I  _ _    I_  I I",
          "I_ _ _ _ _I_ _ _ _I" },

        // maze 4
        { " _ _ _ _ _ _ _ _ _ ",
          "I  _    I  _ _ _  I",
          "I I  _I I I   I   I",
          "I I I  _ _ _I I I I",
          "I  _I   I  _ _I  _I",
          "I I   I  _ _  I   I",
          "I I I I_ _  I I I I",
          "I_  I  _  I I  _I I",
          "I  _I I   I I   I I",
          "I I  _  I I_ _I I I",
          "I I_  I I_ _ _ _I I",
          "I_ _ _I_ _ _ _ _ _I" },

        // maze 5
        { " _ _ _ _ _ _ _ _ _ ",
          "I      _ _  I  _  I",
          "I I I_ _ _ _ _  I I",
          "I I I  _ _    I I I",
          "I I  _I   I I_ _I I",
          "I I I   I I I    _I",
          "I I_ _I I I   I_  I",
          "I I  _ _I I I_  I I",
          "I_  I   I_ _  I I I",
          "I  _I I_ _  I I  _I",
          "I I  _I   I_ _I_  I",
          "I_ _ _ _I_ _ _ _ _I" },

        };

  static private final String kMazeData12x8[][] = {

        // maze 0
        { " _ _ _ _ _ _ _ _ ",
          "I  _ _  I  _    I",
          "I I   I_ _ _ _I I",
          "I I I  _      I I",
          "I I I_ _ _I I  _I",
          "I I I  _ _ _I   I",
          "I I I I  _  I I I",
          "I I I_    I I_  I",
          "I I_  I I I_  I I",
          "I_   _I I I  _I I",
          "I  _  I I I I  _I",
          "I  _ _I_  I I_  I",
          "I_ _ _ _ _I_ _ _I" },
        
        // maze 1
        { " _ _ _ _ _ _ _ _ ",
          "I  _  I  _ _ _  I",
          "I   I I I     I I",
          "I I I_ _I I I_  I",
          "I I  _ _ _I I   I",
          "I_  I  _   _I I I",
          "I  _I I   I    _I",
          "I  _ _I I I I   I",
          "I I  _  I I I I I",
          "I I I  _I I  _I I",
          "I_ _I I   I I   I",
          "I  _ _I I I_ _I I",
          "I_ _ _ _I_ _ _ _I" },

        // maze 2
        { " _ _ _ _ _ _ _ _ ",
          "I  _ _ _  I  _  I",
          "I I  _ _  I I  _I",
          "I I_    I  _I_  I",
          "I   I I_  I   I I",
          "I I_ _  I I I_ _I",
          "I_ _  I I I_ _  I",
          "I  _ _I I_ _  I I",
          "I   I  _ _  I I I",
          "I I I_ _  I I_  I",
          "I  _ _ _ _I_   _I",
          "I I  _ _    I_  I",
          "I_ _ _ _ _I_ _ _I" },
        
        // maze 3
        { " _ _ _ _ _ _ _ _ ",
          "I     I  _ _ _  I",
          "I I I_ _I  _    I",
          "I I  _ _ _I   I I",
          "I_    I   I I I I",
          "I  _I I I_ _I  _I",
          "I I    _ _  I   I",
          "I I I_ _  I I I I",
          "I_   _ _  I  _I I",
          "I  _I   I I   I I",
          "I I   I I_ _I I I",
          "I I I I_ _ _ _I I",
          "I_ _I_ _ _ _ _ _I" },

        // maze 4
        { " _ _ _ _ _ _ _ _ ",
          "I  _  I  _  I   I",
          "I I    _  I I I I",
          "I I I_  I I_ _I I",
          "I I I  _I_  I  _I",
          "I I  _I   I I   I",
          "I I I   I I  _I I",
          "I I_ _I I I I  _I",
          "I I  _ _I I_ _  I",
          "I_  I   I_ _  I I",
          "I  _I I_ _  I I I",
          "I I  _I   I_ _I I",
          "I_ _ _ _I_ _ _ _I" }

        };

  // return a maze object for the specified index
  static public MazeData get(int index) {

    assert( index >= -1 );
    
    if ( index == -1 ) {
      String mazeData[] = null;
      boolean flipXY = false;
      if ( Env.numTilesX() == 10 && Env.numTilesY() == 10 ) {
        mazeData = kTitleData10x10;
      } else if ( Env.numTilesX() == 9 && Env.numTilesY() == 11 ) {
        mazeData = kTitleData11x9;
      } else if ( Env.numTilesX() == 11 && Env.numTilesY() == 9 ) {
        mazeData = kTitleData11x9;
        flipXY = true;
      } else if ( Env.numTilesX() == 8 && Env.numTilesY() == 12 ) {
        mazeData = kTitleData12x8;
      } else if ( Env.numTilesX() == 12 && Env.numTilesY() == 8 ) {
        mazeData = kTitleData12x8;
        flipXY = true;
      }
      return new MazeData( mazeData, flipXY, true, false );
    }
    
    String mazeData[][] = null;
    boolean flipXY = false;
    
    if ( Env.numTilesX() == 10 && Env.numTilesY() == 10 ) {
      mazeData = kMazeData10x10;
    } else if ( Env.numTilesX() == 9 && Env.numTilesY() == 11 ) {
      mazeData = kMazeData11x9;
    } else if ( Env.numTilesX() == 11 && Env.numTilesY() == 9 ) {
      mazeData = kMazeData11x9;
      flipXY = true;
    } else if ( Env.numTilesX() == 8 && Env.numTilesY() == 12 ) {
      mazeData = kMazeData12x8;
    } else if ( Env.numTilesX() == 12 && Env.numTilesY() == 8 ) {
      mazeData = kMazeData12x8;
      flipXY = true;
    }
    assert( mazeData != null );

    boolean flipVert  = true,
            flipHoriz = false;

    int repeats = index/mazeData.length;
    index = Env.fold(index, mazeData.length);
    if ( (repeats & 1) != 0 ) flipHoriz = !flipHoriz;
    if ( (repeats & 2) != 0 ) flipVert = !flipVert;
    
    return new MazeData( mazeData[index], flipXY, flipVert, flipHoriz );
    
  } // get()
  
} // class Mazes
