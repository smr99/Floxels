/*
 *  FlowGrid.java
 *  Copyright Simon Hern 2010
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.floxels;

import java.util.Arrays;

// solution to Poisson's equation at a particular level of refinement 
public class FlowGrid {

  // number of relaxation steps on the grid, depending on refinement
  static private final int kNumSmoothIterations = 1,//!!!2,
                           kNumCoarseIterations = 20;

  // if true then a full solution is only calculated in specified regions
  static private final boolean kUseDesiredSolutionLevels = true;
  
  // the base grid (no refinement)
  private final FlowBlock mBaseBlocks[][];
  private final int mBaseXSize,
                    mBaseYSize;
  
  // refinement factor and level for this grid
  private int mRefineFactor,
              mRefineLevel;

  // size of this particular grid
  private final int mXSize,
                    mYSize;
  
  // the next grid in the multi-grid hierarchy
  private final FlowGrid mCoarserGrid;
  
  // current solution on the grid
  private final float mData[][];
  
  // source term on the grid
  private final float mSource[][];
  
  // constructor
  public FlowGrid(FlowBlock blocks[][], int refineLevel) {
    
    assert( refineLevel >= 0 );
    
    mBaseBlocks = blocks;
    mBaseXSize = mBaseBlocks[0].length;
    mBaseYSize = mBaseBlocks.length;
    
    mRefineLevel = refineLevel;
    mRefineFactor = ( 1 << refineLevel );
    
    mXSize = mRefineFactor * mBaseXSize;
    mYSize = mRefineFactor * mBaseYSize;

    mData = new float[mYSize][mXSize];
    mSource = new float[mYSize][mXSize];

    if ( mRefineLevel > 0 ) {
      mCoarserGrid = new FlowGrid(blocks, mRefineLevel-1);
    } else {
      mCoarserGrid = null;
    }
    
    reset();

  } // constructor

  // set the current solution to zero
  public void reset() {
    
    for ( int ky = 0 ; ky < mYSize ; ky++ ) {
      Arrays.fill(mData[ky], 0.0f);
      Arrays.fill(mSource[ky], 0.0f);
    }
    
    if ( mCoarserGrid != null ) mCoarserGrid.reset();
    
  } // reset()

  // access to the solution data
  public float[][] data() { return mData; }
  
  // access to the source term
  public float[][] source() { return mSource; }
  
  // advance by one step
  public void solve() {

    if ( mCoarserGrid != null ) {
      restrict();
      mCoarserGrid.solve();
      prolongate();
      for ( int n = 0 ; n < kNumSmoothIterations ; n++ ) smoothSolution();
    } else {
      for ( int n = 0 ; n < kNumCoarseIterations ; n++ ) smoothSolution();
      normalizeSolution();
    }
    
  } // solve()

  // fine-to-coarse interpolation (source data)
  private void restrict() {
    
    assert( mCoarserGrid != null );
    
    float coarseSource[][] = mCoarserGrid.source();

    for ( int iy = 0, ky = 0 ; iy < mYSize ; iy+=2, ky++ ) {
      for ( int ix = 0, kx = 0 ; ix < mXSize ; ix+=2, kx++ ) {
        coarseSource[ky][kx] 
                    = 0.25f*( mSource[iy][ix] + mSource[iy][ix+1] 
                            + mSource[iy+1][ix] + mSource[iy+1][ix+1] );
      }
    }
        
  } // restrict()
  
  // coarse-to-fine interpolation (solution data)
  private void prolongate() {
    
    assert( mCoarserGrid != null );
    
    float coarseData[][] = mCoarserGrid.data();
    
    for ( int iy = 0, ky = 0 ; iy < mYSize ; iy+=2, ky++ ) {
      for ( int ix = 0, kx = 0 ; ix < mXSize ; ix+=2, kx++ ) {
        mData[iy][ix] = mData[iy][ix+1] 
          = mData[iy+1][ix] = mData[iy+1][ix+1] = coarseData[ky][kx];
      }
    }
    
  } // prolongate()
  
  // improve the current solution
  public void smoothSolution() {
    
    if ( mRefineLevel == 0 ) smoothSolutionBase();
    else                     smoothSolutionRefined();
    
  } // smoothSolution()
  
  // improve the current solution (for an unrefined grid)
  private void smoothSolutionBase() {

    assert( mRefineLevel == 0 );
    
    final float delta = 1.0f;
    final float sourceFactor = delta*delta/4.0f;
    
    for ( int stagger = 0 ; stagger <= 1 ; stagger++ ) {
      for ( int ky = 0 ; ky < mYSize ; ky++ ) {

        final int kx0 = ((stagger+ky) % 2);
        for ( int kx = kx0 ; kx < mXSize ; kx+=2 ) {
          final FlowBlock block = mBaseBlocks[ky][kx];
          
          final float phi0 = mData[ky][kx];
          final float phiN = block.boundaryNorth() 
                             ? ( phi0 - block.inFlowNorth() )
                             : mData[ky-1][kx];
          final float phiS = block.boundarySouth() 
                             ? ( phi0 - block.inFlowSouth() )
                             : mData[ky+1][kx];
          final float phiE = block.boundaryEast() 
                             ? ( phi0 - block.inFlowEast() )
                             : mData[ky][kx+1];
          final float phiW = block.boundaryWest() 
                             ? ( phi0 - block.inFlowWest() )
                             : mData[ky][kx-1];
 
          mData[ky][kx] = 0.25f*( phiN + phiS + phiE + phiW )
                          - sourceFactor*mSource[ky][kx];
        }
      }
    }
    
  } // smoothSolutionBase()
  
  // improve the current solution (for a refined grid)
  private void smoothSolutionRefined() {

    assert( mRefineLevel > 0 );
    
    final float delta = 1.0f/mRefineFactor;
    final float sourceFactor = delta*delta/4.0f;
    
    for ( int ky = 0 ; ky < mBaseYSize ; ky++ ) {
      for ( int kx = 0 ; kx < mBaseXSize ; kx++ ) {
        
        final FlowBlock block = mBaseBlocks[ky][kx];
        
        if ( kUseDesiredSolutionLevels &&
             block.desiredSolutionLevel() < mRefineLevel ) continue;

        final int iy0 = ( ky << mRefineLevel ),
                  iy1 = iy0 + mRefineFactor-1,
                  ix0 = ( kx << mRefineLevel ),
                  ix1 = ix0 + mRefineFactor-1;
        
        for ( int stagger = 0 ; stagger <= 1 ; stagger++ ) {
          for ( int iy = iy0 ; iy <= iy1 ; iy++ ) {
            for ( int ix = ix0 + ((stagger+iy)%2) ; ix <= ix1 ; ix+=2 ) {
              final float phi0 = mData[iy][ix];
              final float phiN = ( block.boundaryNorth() && iy==iy0 ) 
                                 ? ( phi0 - delta*block.inFlowNorth() )
                                 : mData[iy-1][ix];
              final float phiS = ( block.boundarySouth() && iy==iy1 ) 
                                 ? ( phi0 - delta*block.inFlowSouth() )
                                 : mData[iy+1][ix];
              final float phiE = ( block.boundaryEast() && ix==ix1 ) 
                                 ? ( phi0 - delta*block.inFlowEast() )
                                 : mData[iy][ix+1];
              final float phiW = ( block.boundaryWest() && ix==ix0 ) 
                                 ? ( phi0 - delta*block.inFlowWest() )
                                 : mData[iy][ix-1];
              mData[iy][ix] = 0.25f*( phiN + phiS + phiE + phiW )
                              - sourceFactor*mSource[iy][ix];
            }
          }
        }
          
      } // for kx
    } // for ky
    
  } // smoothSolutionRefined()

  // adjust the solution so its mean is close to zero
  // (adding a constant does not affect the gradient)
  private void normalizeSolution() {
    
    float sum = 0.0f;
    for ( int ky = 0 ; ky < mYSize ; ky++ ) {
      for ( int kx = 0 ; kx < mXSize ; kx++ ) {
        sum += mData[ky][kx]; 
      }
    }
    
    final float mean = sum/(mYSize*mXSize);
    for ( int ky = 0 ; ky < mYSize ; ky++ ) {
      for ( int kx = 0 ; kx < mXSize ; kx++ ) {
        mData[ky][kx] -= mean; 
      }
    }
    
  } // normalizeSolution()
  
} // class FlowGrid
