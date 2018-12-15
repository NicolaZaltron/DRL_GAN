import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.*;

public class ZeldaLevelWallCounter {

    public static class Pair<integer> {
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
            return this.x.equals(pairo.getX()) && this.y.equals(pairo.getY());
        }
    }

    public static void main(String[] args) throws IOException {
        String dir = System.getProperty("user.dir");
        System.out.println("Working Directory = " + dir);
        String inputDirectory = dir + "/TestDifficulty/4";

        /*TODO load levels from files */
        File file = new File(inputDirectory);
        String[] fileList = file.list();

        int levelwithfewwalls = 0;
        int levelwithaveragewalls = 0;
        int levelwithmanywalls = 0;

        for (String inputFile : fileList) {
            try {
                System.out.println("Reading: " + inputFile + "  "+  inputFile.substring(inputFile.length() - 4));
                if((inputFile.length() > 4) && (inputFile.substring(inputFile.length() - 4).equals(".txt"))){

                    int[][] level = readLevel(new Scanner(new FileInputStream(inputDirectory + "\\" + inputFile)));
                    System.out.println(level);
                    System.out.println("Read: " + inputFile);

                    int keyCount = 0;
                    int doorCount = 0;
                    int playerCount = 0;
                    int wallsCount = 0;
                    int enemiesCount = 0;
                    int difficulty = 0;
                    boolean playable = true;

                    for (int k = 0; k < 13; k++) {
                        for (int j = 0; j < 9; j++) {
                            if(k == 0 || k == 12) {
                                if(level[j][k]!=0) {
                                    playable = false;
                                }
                            } else {
                                if(j == 0  || j == 8) {
                                    if(level[j][k]!=0) {
                                        playable = false;
                                    }
                                }
                            }
                        }
                    }
                    if(playable) {
                        for (int k = 1; k < 12; k++) { 
                            for (int j = 1; j < 8; j++) {
                                if(level[j][k]==2) keyCount++;
                                if(level[j][k]==3) doorCount++;
                                if(level[j][k]==7) playerCount++;
                                if(level[j][k]==0) wallsCount++;
                                if(level[j][k]==4 || level[j][k]==5 || level[j][k]==6) enemiesCount++;
                            }
                        }
                        //System.out.println(wallsCount);
                        if(wallsCount<7){
                        	levelwithfewwalls++;
                        } else if (wallsCount<=21) {
                        	levelwithaveragewalls++;
                        } else {
                        	levelwithmanywalls++;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } //close big for

        System.out.println("Levels with fewer than 7 walls " + levelwithfewwalls);
        System.out.println("Levels with average amount of walls " + levelwithaveragewalls);
        System.out.println("Levels with many walls " + levelwithmanywalls);
        return;
    }


    public static Boolean simpleTestLevel(int[][] level) {
        for (int k = 0; k < 13; k++) {
            for (int j = 0; j < 9; j++) {
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
                                if(level[x][y-1]!=3){   //don't expand the node if it is a door
                                    Pair<Integer> newPair = new Pair<Integer>(x, y-1);
                                    if(!closedList.contains(newPair) && !openList.contains(newPair)) {
                                        openList.add(newPair);
                                    }
                                }
                            }
                        }
                        if(y<13) {
                            if(level[x][y+1]!=0) {
                                if(level[x][y+1]==2) keyReachable = true;
                                if(level[x][y+1]==3) doorReachable = true;
                                if(level[x][y+1]!=3){   //don't expand the node if it is a door
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
                                if(level[x-1][y]!=3){   //don't expand the node if it is a door
                                    Pair<Integer> newPair = new Pair<Integer>(x-1, y);
                                    if(!closedList.contains(newPair) && !openList.contains(newPair)) {
                                        openList.add(newPair);
                                    }
                                }
                            }
                        }
                        if(x<9) {
                            if(level[x+1][y]!=0) {
                                if(level[x+1][y]==2) keyReachable = true;
                                if(level[x+1][y]==3) doorReachable = true;
                                if(level[x+1][y]!=3){   //don't expand the node if it is a door
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

    public static int[][] readLevel(Scanner scanner) throws Exception {
        String line;
        ArrayList<String> lines = new ArrayList<>();
        int width = 0;
        while (scanner.hasNext()) {
            line = scanner.nextLine();
            width = line.length();
            lines.add(line);
            // System.out.println(line);
        }
        int[][] a = new int[lines.size()][width];
        System.out.println("Arrays length: " + a.length);
        for (int y = 0; y < lines.size(); y++) {
            System.out.println("Processing line: " + lines.get(y));
            for (int x = 0; x < width; x++) {
                try { // Added error checking to deal with unrecognized tile types
                    a[y][x] = zeldaCharToInt(lines.get(y).charAt(x));
                } catch (Exception e) {
                    System.out.println("Problem on ");
                    System.out.println("\ty = " + y);
                    System.out.println("\tx = " + x);
                    System.out.println("\tlines.get(y).charAt(x) = " + lines.get(y).charAt(x));
                    System.exit(1);
                }
            }
        }
        return a;
    }

    public static int zeldaCharToInt(char a){
        switch (a){
            case 'w': return 0;
            case 'A': return 7;
            case '+': return 2;
            case 'g': return 3;
            case '1': return 4; // Enemy
            case '2': return 5; // Enemy
            case '3': return 6; // Enemy
            case '.': return 1; // Default
        }
        return 0;
    }

    public static char convertToASCII(int a){
        switch (a){
           case 0: return 'w';
           case 7: return 'A';
           case 2: return '+';
           case 3: return 'g';
           case 4: return '1'; // Enemy
           case 5: return '2'; // Enemy
           case 6: return '3'; // Enemy
           case 1: return '.'; // Default
        }
        return 'w';
    }

}
