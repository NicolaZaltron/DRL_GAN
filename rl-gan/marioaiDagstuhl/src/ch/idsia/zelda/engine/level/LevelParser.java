/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.zelda.engine.level;

import ch.idsia.zelda.engine.sprites.Enemy;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vv
 */
public class LevelParser {
    
	public static final int BUFFER_WIDTH = 15; // This is the extra space added at the start and ends of levels
	
    /**
     *
     * @param args
     */
    public LevelParser(){
        
    }
    
    
    /*"tiles" : {
    0    "X" : ["solid","ground"],
    1    "S" : ["solid","breakable"],
    2    "-" : ["passable","empty"],
    3    "?" : ["solid","question block", "full question block"],
    4    "Q" : ["solid","question block", "empty question block"],
    5    "E" : ["enemy","damaging","hazard","moving"],
    6    "<" : ["solid","top-left pipe","pipe"],
    7    ">" : ["solid","top-right pipe","pipe"],
    8    "[" : ["solid","left pipe","pipe"],
    9    "]" : ["solid","right pipe","pipe"],
    10   "o" : ["coin","collectable","passable"]
    
    // These last two were not present in the json description from VDLC, but were present in the data
    
    11   "B" : Top of a Bullet Bill cannon, solid
    12   "b" : Body/support of a Bullet Bill cannon, solid
    */
    
    public Level test(){
        Level level = new Level(202,14);
        level.setBlock(1, 13, (byte) 9);
        level.setBlock(2, 13, (byte) 9);
        level.setBlock(3, 13, (byte) 9);
        level.setBlock(4, 13, (byte) 9);
        level.setBlock(5, 13, (byte) 9);
        level.setBlock(6, 13, (byte) 9);
        level.setBlock(7, 13, (byte) 9);
        
        return level;
    }

    /**
     * This method doesn't seem to be used anywhere. I guess it was completely
     * replaced by the createLevelJson method?
     * @param filename
     * @return
     */
    public static Level createLevelASCII(String filename)
    {
    	//Read in level representation
    	ArrayList<String> lines = new ArrayList<String>();
    	try {
    		File file = new File(filename);
    		FileReader fileReader = new FileReader(file);
    		BufferedReader bufferedReader = new BufferedReader(fileReader);
    		String line;
    		while ((line = bufferedReader.readLine()) != null) {
    			lines.add(line);
    		}
    		fileReader.close();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}

    	int width = lines.get(0).length();
    	int height = lines.size();
    	// For a buffer at both the start and the end of each level
    	int extraStones = BUFFER_WIDTH;
    	Level level = new Level(width+2*extraStones,height);

    	//Set Level Exit
    	//Extend level by that
    	level.xExit = width+extraStones+1;
    	level.yExit = height-1;

    	for(int i=0; i<extraStones; i++){
    		level.setBlock(i, height-1, (byte) 9);
    	}

    	for(int i=0; i<extraStones; i++){
    		level.setBlock(width+i+extraStones, height-1, (byte) 9);
    	}

    	//set Level map
    	for(int i=0; i<height; i++){
    		for(int j=0; j<lines.get(i).length(); j++){
    			String code = String.valueOf(lines.get(i).charAt(j));
    			if("E".equals(code)){
    				//set Enemy
    				//new SpriteTemplate(type, boolean winged)
    				level.setSpriteTemplate(j+extraStones, i, new SpriteTemplate(Enemy.ENEMY_FAST, false));
    				//System.out.println("j: "+j+" i:"+i);
    				//set passable tile: everything not set is passable
    			}else{
    				int encoded = codeParserASCII(code);
    				if(encoded !=0){
    					level.setBlock(j+extraStones, i, (byte) encoded);
    					//System.out.println("j: "+j+" i:"+i+" encoded: "+encoded);
    				}
    			}
    		}
    	}
    	return level;
    }


    public static Level createLevelJson(List<List<Integer>> input)
    {
    	int width = input.get(0).size();
    	int height = input.size();
    	int extraStones = BUFFER_WIDTH;
    	Level level = new Level(width+2*extraStones,height);

        //Set Level Exit
        //Extend level by that
        level.xExit = width+extraStones+1; // Push exit point over by 1 so that goal post does not overlap with other level sprites
        level.yExit = height-1;
        
        for(int i=0; i<extraStones; i++){
            level.setBlock(i, height-1, (byte) 9);
        }
        for(int i=0; i<extraStones; i++){
            level.setBlock(width+i+extraStones, height-1, (byte) 9);
        }
        
        //set Level map
        for(int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                int code = input.get(i).get(j);
                if(5==code){
                    //set Enemy
                    //new SpriteTemplate(type, boolean winged)
                    level.setSpriteTemplate(j+extraStones, i, new SpriteTemplate(Enemy.ENEMY_FAST, false));
                    //System.out.println("j: "+j+" i:"+i);
                    //set passable tile: everything not set is passable
                }else{
                    int encoded = codeParser(code);
                    if(encoded !=0){
                        level.setBlock(j+extraStones, i, (byte) encoded);
                        //System.out.println("j: "+j+" i:"+i+" encoded: "+encoded);
                    }
                }
            }
        }
                
        return level;
    }
    
    
     
    public static int codeParser(int code){
        int output = 0;
        switch(code){
        case 0: output = 0; break;	//Wall
        case 1: output = 0; break;	//Zelda Spawn
        case 2: output = 0; break;	//Key
        case 3: output = 0; break;	//Exit
        case 4: output = 0; break;	//Monster 1
        case 5: output = 0; break;	//Monster 2
        case 6: output = 0; break;	//Monster 3
        default: output=0; break; 	//Empty
        }
        return output;
    }
    
    public static int codeParserASCII(String code){
        int output = 0;
        switch(code){
            case "w": output = 0; break;	//Wall
            case "A": output = 0; break;	//Zelda Spawn
            case "+": output = 0; break;	//Key
            case "g": output = 0; break;	//Exit
            case "1": output = 0; break;	//Monster 1
            case "2": output = 0; break;	//Monster 2
            case "3": output = 0; break;	//Monster 3
            default: output=0; break; 		//Empty
        }
        return output;
    }

}
