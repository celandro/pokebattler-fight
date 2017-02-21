package POGOProtos;

import java.io.File;

public class RenamePNGs {
    public static void main(String... arg) {
        File directory = new File("E:\\PokeBattler\\pokebattlerFront\\public\\img");
        // bulbabpedia
//        File[] images = directory.listFiles(file -> {return file.getName().startsWith("120px") && file.getName().endsWith(".png");});
//        for (File f: images) {
//            String name = f.getName();
//            File newFile = new File(directory, Character.toLowerCase(name.charAt(9)) + name.substring(10));
//            System.out.println("Renaming " + f  + " to " + newFile);
//            f.renameTo(newFile);
//        }
        File[] images = directory.listFiles(file -> {return file.getName().endsWith(".svg") && file.getName().charAt(3) == '-';});
        for (File f: images) {
            String name = f.getName();
            File newFile = new File(directory, name.substring(4));
            System.out.println("Renaming " + f  + " to " + newFile);
            f.renameTo(newFile);
        }
        
    }
}
