package cmatest;

import static basicMap.Settings.DEBUG_MSG;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import basicMap.Settings;
import fr.inria.optimization.cmaes.CMAEvolutionStrategy;
import fr.inria.optimization.cmaes.fitness.IObjectiveFunction;

public class CMAMarioSolverEXPORT {
	
	static int duplicateCounter = 0;
	static int unplayableCounter = 0;
	
	public class Pair<integer> {

		  private final integer x;
		  private final integer y;

		  public Pair( integer x, integer y) {
		    this.x = x;
		    this.y = y;
		  }

		  public integer getX() { return x; }
		  public integer getY() { return y; }

		  @Override
		  public int hashCode() { return x.hashCode() ^ y.hashCode(); }

		  @Override
		  public boolean equals(Object o) {
		    if (!(o instanceof Pair)) return false;
		    Pair pairo = (Pair) o;
		    return this.x.equals(pairo.getX()) &&
		           this.y.equals(pairo.getY());
		  }

		}
	
	public static HashSet<String> alreadyExported;
	
	// Sebastian's Wasserstein GAN expects latent vectors of length 32
	public static final int Z_SIZE = 32; // length of latent space vector
	public static final int EVALS = 5000;
	public static final double STOFITNESS = -1e6;//  1e-14; 
	public static final int LOOPS = 1000000;

	
    public static void main(String[] args) throws IOException {
        Settings.setPythonProgram();
        //int loops = 10000;
        double[][] bestX = new double[LOOPS][32];
        double[] bestY = new double[LOOPS];
        MarioEvalFunction marioEvalFunction = new MarioEvalFunction();
        ZeldaEvalFunction zeldaEvalFunction = new ZeldaEvalFunction();
        for(int i=0; i<LOOPS; i++){
            System.out.println("Iteration:"+ i);
            CMAMarioSolverEXPORT solver = new CMAMarioSolverEXPORT(marioEvalFunction, Z_SIZE, EVALS);
            
            solver.export(marioEvalFunction, i);
            //double[] solution = solver.run(print_line);
            
            /*System.out.println("SOLUTION PRINT: " + solution);
            System.out.println("Best solution = " + Arrays.toString(MarioEvalFunction.mapArrayToOne(solution)));
            bestX[i] = MarioEvalFunction.mapArrayToOne(solution);
            bestY[i] = solver.fitFun.valueOf(MarioEvalFunction.mapArrayToOne(solution));*/
        }
        marioEvalFunction.exit();
        System.out.println("Done");
        System.exit(0);
    }

    IObjectiveFunction fitFun;
    int nDim;
    CMAEvolutionStrategy cma;

    public CMAMarioSolverEXPORT(IObjectiveFunction fitFun, int nDim, int maxEvals) {
        this.fitFun = fitFun;
        this.nDim = nDim;
        cma = new CMAEvolutionStrategy();
        cma.readProperties(); // read options, see file CMAEvolutionStrategy.properties
        cma.setDimension(nDim); // overwrite some loaded properties
        cma.setInitialX(-1,1); // set initial seach point xmean coordinate-wise uniform between l and u, dimension needs to have been set before
        cma.setInitialStandardDeviation(1/Math.sqrt(nDim)); // also a mandatory setting
        //cma.options.stopFitness = -1e6; // 1e-14;       // optional setting
        cma.options.stopFitness = this.STOFITNESS;        // optional setting
        // cma.options.stopMaxIter = 100;
        cma.options.stopMaxFunEvals = maxEvals;
        System.out.println("Diagonal: " + cma.options.diagonalCovarianceMatrix);
        if (alreadyExported==null)
        	alreadyExported = new HashSet<String>();
    }

    public void setDim(int n) {
        cma.setDimension(n);
    }

    /*public void setInitialX(double x) {
        cma.setInitialX(x);
    }*/

    public void setObjective(IObjectiveFunction fitFun) {
        this.fitFun = fitFun;
    }

    public void setMaxEvals(int n) {
        cma.options.stopMaxFunEvals = n;
    }
    
    public double[][] export(MarioEvalFunction eval, int iter) throws IOException {
    	
    	// new a CMA-ES and set some initial values
    	
    	

        // initialize cma and get fitness array to fill in later
        double[] fitness = cma.init();  // new double[cma.parameters.getPopulationSize()];

        // initial output to files
        cma.writeToDefaultFilesHeaders(0); // 0 == overwrites old files

        // iteration loop
        //while (cma.stopConditions.getNumber() == 0) {

            // --- core iteration step ---
        double[][] pop = cma.samplePopulation(); // get a new population of solutions
        
        new File("GeneratedLevels/gan/0").mkdirs();
        new File("GeneratedLevels/gan/1").mkdirs();
        new File("GeneratedLevels/gan/2").mkdirs();
		new File("GeneratedLevels/gan/3").mkdirs();
		new File("GeneratedLevels/gan/4").mkdirs();
		new File("GeneratedLevels/gan/5").mkdirs();
		new File("GeneratedLevels/gan/6").mkdirs();
		new File("GeneratedLevels/gan/7").mkdirs();
		new File("GeneratedLevels/gan/8").mkdirs();
		new File("GeneratedLevels/gan/9").mkdirs();
		
        
        for (int i = 0; i < pop.length; ++i) {    // for each candidate solution i
        	//print_line.println(Arrays.toString(pop[i]));//THIS IS THE LATENT VECTORS
            //double[] solution = solver.run(print_line);
            int keyCount = 0;
            int doorCount = 0;
            int playerCount = 0;
            int wallsCount = 0;
            int enemiesCount = 0;
            int difficulty = 0;
            boolean playable = true;
            if(pop[i].length==32) {
	    		byte[][] level = eval.levelFromLatentVectorZelda(pop[i]).map;
	    		byte[][] levelTransposed = new byte[9][13];
	    		
	    		for (int k = 0; k < 9; k++) {
	    			for (int j = 0; j < 13; j++) {
	    				if(j == 0 || j == 12) {
	    					if(level[j][k]!=0) {
	    						playable = false;
	    					}
	    				} else {
	    					if(k == 0  || k == 8) {
	    						if(level[j][k]!=0) {
		    						playable = false;
		    					}
	    					}
	    				}
	    			}
	    		}
	    		if(playable) {
		    		for (int k = 1; k < 8; k++) {
		    			for (int j = 1; j < 12; j++) {
		    				if(level[j][k]==2) keyCount++;
		    				if(level[j][k]==3) doorCount++;
		    				if(level[j][k]==7) playerCount++;
		    				if(level[j][k]==0) wallsCount++;
		    				if(level[j][k]==4 || level[j][k]==5 || level[j][k]==6) enemiesCount++;
		    				levelTransposed[k][j] = level[j][k];
		    			}
					}
		    		//System.out.println(wallsCount);
		    		if(keyCount==1 && doorCount==1 && playerCount==1) {
		    			String stringifiedLevel = "";
		    			for (int j = 0; j < levelTransposed.length; j++) {
		    				for (int k = 0; k < levelTransposed[j].length; k++) {
		    					stringifiedLevel += (convertToASCII(levelTransposed[j][k]));
		    				}
		    			}
		    			if(!alreadyExported.contains(stringifiedLevel.toString())) {
		    				System.out.println(stringifiedLevel);
		    				//System.out.println("doesn't contain");
		    				//40 border walls in each level
			    			if(simpleTestLevel(level)) {
			    				//System.out.println();
			    				//System.out.println("playable");
			    				//System.out.println();
				    			FileWriter write;
				    			switch (enemiesCount) {
				    				case 0: 	if(wallsCount<7) { difficulty = 0;}
				    							else if(wallsCount>21) { difficulty = 2;}
				    							else {difficulty = 1;}	
				    							break;
				    				case 1: 	if(wallsCount<7) { difficulty = 2;}
				    							else if(wallsCount>21) { difficulty = 4;}
	    										else {difficulty = 3;}	
	    										break;
				    				case 2: 	if(wallsCount<7) { difficulty = 4;}
				    							else if(wallsCount>21) { difficulty = 6;}
												else {difficulty = 5;}	
												break;
				    				case 3: 	if(wallsCount<7) { difficulty = 6;}
				    							else if(wallsCount>21) { difficulty = 8;}
												else {difficulty = 7;}	
												break;
				    				default: 	difficulty = 9; break;
				    			}
				    			switch (difficulty) {
				    				case 0: 	write = new FileWriter("GeneratedLevels/gan/0/timeline"+iter+"-"+ i + ".txt", true); break;
				    				case 1: 	write = new FileWriter("GeneratedLevels/gan/1/timeline"+iter+"-"+ i + ".txt", true); break;
				    				case 2: 	write = new FileWriter("GeneratedLevels/gan/2/timeline"+iter+"-"+ i + ".txt", true); break;
				    				case 3: 	write = new FileWriter("GeneratedLevels/gan/3/timeline"+iter+"-"+ i + ".txt", true); break;
				    				case 4: 	write = new FileWriter("GeneratedLevels/gan/4/timeline"+iter+"-"+ i + ".txt", true); break;
				    				case 5: 	write = new FileWriter("GeneratedLevels/gan/5/timeline"+iter+"-"+ i + ".txt", true); break;
				    				case 6: 	write = new FileWriter("GeneratedLevels/gan/6/timeline"+iter+"-"+ i + ".txt", true); break;
				    				case 7: 	write = new FileWriter("GeneratedLevels/gan/7/timeline"+iter+"-"+ i + ".txt", true); break;
				    				case 8: 	write = new FileWriter("GeneratedLevels/gan/8/timeline"+iter+"-"+ i + ".txt", true); break;
				    				case 9: 	write = new FileWriter("GeneratedLevels/gan/9/timeline"+iter+"-"+ i + ".txt", true); break;
				    				default: 	write = new FileWriter("GeneratedLevels/gan/9/timeline"+iter+"-"+ i + ".txt", true); break;
				    			}
				    			PrintWriter print_line = new PrintWriter(write);
				    			for (int j = 0; j < levelTransposed.length; j++) {
				    				for (int k = 0; k < levelTransposed[j].length; k++) {
				    					print_line.print(convertToASCII(levelTransposed[j][k]));
				    				}
				    				print_line.println();
				    			}
				    			print_line.close();
				    			
			    			} else {
			    				unplayableCounter ++;
			    				//System.out.println("unplayable. "+ unplayableCounter +" unplayables in total");
			    			}
			    			alreadyExported.add(stringifiedLevel.toString());
		    			} else {
		    				duplicateCounter++;
		    				//System.out.println("already contains . "+ duplicateCounter +" duplicates in total");
		    			}
		    		} else {
		    			unplayableCounter ++;
	    				//System.out.println("unplayable. "+ unplayableCounter +" unplayables in total");
		    		}
	    		} else {
	    			unplayableCounter ++;
    				//System.out.println("unplayable due to walls. "+ unplayableCounter +" unplayables in total");
	    		}
            }
    		//print_line.print(eval.levelFromLatentVector(pop[i]));
            //fitness[i] = pop[i]
            System.out.println("unplayable:"+ unplayableCounter +" , exported:"+ alreadyExported.size() + " ,duplicates:" + duplicateCounter);
        }
        //cma.writeToDefaultFiles();
        

        //}
        
    	return new double[0][0];
    }
    
    private Boolean simpleTestLevel(byte[][] level) {
    	for (int k = 0; k < 9; k++) {
			for (int j = 0; j < 13; j++) {
				//expand starting from the player
				if(level[j][k]==7) {
					boolean keyReachable = false;
					boolean doorReachable = false;
					ArrayList<Pair<Integer>> openList = new ArrayList<>();
					ArrayList<Pair<Integer>> closedList = new ArrayList<>();
					openList.add(new Pair<Integer>(j, k));
					while (!openList.isEmpty() && !(keyReachable && doorReachable)) {
						int x = openList.get(0).getX();
						int y = openList.get(0).getY();
						closedList.add(openList.get(0));
						openList.remove(0);
						if(y>0) {
							if(level[x][y-1]!=0) {
								if(level[x][y-1]==2) keyReachable = true;
			    				if(level[x][y-1]==3) doorReachable = true;
			    				if(level[x][y-1]!=3){	//don't expand the node if it is a door
				    				Pair<Integer> newPair = new Pair<Integer>(x, y-1);
				    				if(!closedList.contains(newPair) && !openList.contains(newPair)) {
				    					openList.add(newPair);
				    				}
			    				}
							}
						}
						if(y<9) {
							if(level[x][y+1]!=0) {
								if(level[x][y+1]==2) keyReachable = true;
			    				if(level[x][y+1]==3) doorReachable = true;
			    				if(level[x][y+1]!=3){	//don't expand the node if it is a door
				    				Pair<Integer> newPair = new Pair<Integer>(x, y+1);
				    				if(!closedList.contains(newPair) && !openList.contains(newPair)) {
				    					openList.add(newPair);
				    				}
			    				}
							}
						}
						if(x>0) {
							if(level[x-1][y]!=0) {
								if(level[x-1][y]==2) keyReachable = true;
			    				if(level[x-1][y]==3) doorReachable = true;
			    				if(level[x-1][y]!=3){	//don't expand the node if it is a door
				    				Pair<Integer> newPair = new Pair<Integer>(x-1, y);
				    				if(!closedList.contains(newPair) && !openList.contains(newPair)) {
				    					openList.add(newPair);
				    				}
			    				}
							}
						}
						if(x<13) {
							if(level[x+1][y]!=0) {
								if(level[x+1][y]==2) keyReachable = true;
			    				if(level[x+1][y]==3) doorReachable = true;
			    				if(level[x+1][y]!=3){	//don't expand the node if it is a door
				    				Pair<Integer> newPair = new Pair<Integer>(x+1, y);
				    				if(!closedList.contains(newPair) && !openList.contains(newPair)) {
				    					openList.add(newPair);
				    				}
			    				}
							}
						}
					}
					if(keyReachable && doorReachable) {
						return true;
					}
				}
			}
		}
    	return false;
    }
    
    private String convertToASCII(int code) {
    	switch(code) {
    	 case 0: return "w"; //Wall
         case 1: return "."; 
         case 2: return "+"; 
         case 3: return "g"; 
         case 4: return "1";
         case 5: return "2";
         case 6: return "3"; 
         case 7: return "A"; 
         default: return ".";
    	}
    }

    public double[] run(PrintWriter print_line) {

        // new a CMA-ES and set some initial values

        // initialize cma and get fitness array to fill in later
        double[] fitness = cma.init();  // new double[cma.parameters.getPopulationSize()];

        // initial output to files
        cma.writeToDefaultFilesHeaders(0); // 0 == overwrites old files

        // iteration loop
        while (cma.stopConditions.getNumber() == 0) {

            // --- core iteration step ---
            double[][] pop = cma.samplePopulation(); // get a new population of solutions
            for (int i = 0; i < pop.length; ++i) {    // for each candidate solution i
                // a simple way to handle constraints that define a convex feasible domain
                // (like box constraints, i.e. variable boundaries) via "blind re-sampling"
                // assumes that the feasible domain is convex, the optimum is
                while (!fitFun.isFeasible(pop[i])) {    //   not located on (or very close to) the domain boundary,
                    System.out.println(DEBUG_MSG + "Not in feasible domain. Will resample once.");
                    pop[i] = cma.resampleSingle(i);    //   initialX is feasible and initialStandardDeviations are
                    //   sufficiently small to prevent quasi-infinite looping here
                    // compute fitness/objective value
                }
                fitness[i] = fitFun.valueOf(pop[i]); // fitfun.valueOf() is to be minimized
                System.out.println(fitness[i]);
                print_line.println(Arrays.toString(pop[i])+ " : " + fitness[i]);
            }
            cma.updateDistribution(fitness);         // pass fitness array to update search distribution
            // --- end core iteration step ---

            // output to files and console
            cma.writeToDefaultFiles();
            int outmod = 150;
            if (cma.getCountIter() % (15 * outmod) == 1) {
                // cma.printlnAnnotation(); // might write file as well
            }
            if (cma.getCountIter() % outmod == 1) {
                // cma.println();
            }
        }
        // evaluate mean value as it is the best estimator for the optimum
        // cma.setFitnessOfMeanX(fitFun.valueOf(cma.getMeanX())); // updates the best ever solution

        // final output
        cma.writeToDefaultFiles(1);
        cma.println();
        cma.println("Terminated due to");
        for (String s : cma.stopConditions.getMessages())
            cma.println("  " + s);
        cma.println("best function value " + cma.getBestFunctionValue()
                + " at evaluation " + cma.getBestEvaluationNumber());

        // System.out.println("Best solution is: " + Arrays.toString(cma.getBestX()));
        // we might return cma.getBestSolution() or cma.getBestX()
        // return cma.getBestX();
        cma.setFitnessOfMeanX(fitFun.valueOf(cma.getMeanX())); // updates the best ever solution
        return cma.getBestX();
        //return cma.getBestRecentX();

    }



}

