package POGOProtos;

import java.io.File;

public class RenamePNGs {
    public static void main(String... arg) {
        File directory = new File("E:\\PokeBattler\\pokebattlerFront\\public\\img");
        File[] images = directory.listFiles(file -> {return file.getName().startsWith("120px") && file.getName().endsWith(".png");});
        for (File f: images) {
            String name = f.getName();
            File newFile = new File(directory, Character.toLowerCase(name.charAt(9)) + name.substring(10));
            System.out.println("Renaming " + f  + " to " + newFile);
            f.renameTo(newFile);
        }
    }
}
