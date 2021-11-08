import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOError;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Bulk_File_Renamer {
    public static Comparator<File> AlphanumComparator = new Comparator<File>() {
        private final boolean isDigit(char ch) {
            return ch >= '0' && ch <= '9';
        }

        private final String getChunk(String s, int slength, int marker) {
            StringBuilder chunk = new StringBuilder();
            char c = s.charAt(marker);
            chunk.append(c);
            ++marker;
            if (this.isDigit(c)) {
                while (marker < slength) {
                    c = s.charAt(marker);
                    if (!this.isDigit(c)) {
                        break;
                    }

                    chunk.append(c);
                    ++marker;
                }
            } else {
                while (marker < slength) {
                    c = s.charAt(marker);
                    if (this.isDigit(c)) {
                        break;
                    }

                    chunk.append(c);
                    ++marker;
                }
            }

            return chunk.toString();
        }

        public int compare(File f1, File f2) {
            if (f1 != null && f2 != null) {
                String s1 = f1.getName();
                String s2 = f2.getName();
                int thisMarker = 0;
                int thatMarker = 0;
                int s1Length = s1.length();
                int s2Length = s2.length();

                while (thisMarker < s1Length && thatMarker < s2Length) {
                    String thisChunk = this.getChunk(s1, s1Length, thisMarker);
                    thisMarker += thisChunk.length();
                    String thatChunk = this.getChunk(s2, s2Length, thatMarker);
                    thatMarker += thatChunk.length();
                    //int result = false;
                    int resultx;
                    if (this.isDigit(thisChunk.charAt(0)) && this.isDigit(thatChunk.charAt(0))) {
                        int thisChunkLength = thisChunk.length();
                        resultx = thisChunkLength - thatChunk.length();
                        if (resultx == 0) {
                            for (int i = 0; i < thisChunkLength; ++i) {
                                resultx = thisChunk.charAt(i) - thatChunk.charAt(i);
                                if (resultx != 0) {
                                    return resultx;
                                }
                            }
                        }
                    } else {
                        resultx = thisChunk.compareTo(thatChunk);
                    }

                    if (resultx != 0) {
                        return resultx;
                    }
                }

                return s1Length - s2Length;
            } else {
                return 0;
            }
        }
    };

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Range Changer?");
        if (!scanner.nextLine().toLowerCase().equals(""))
            while (true) {
                int count = 0;
                System.out.println();
                System.out.println("Old range:");
                String oldR = scanner.nextLine();
                System.out.println("Shift:");
                int shift = Integer.parseInt(scanner.nextLine());
                ArrayList<File> files = new ArrayList<>(Arrays.asList((new File(".")).listFiles()));
                if (files == null) {
                    throw new IOError(new IOException("HEH, This one wasn't my fault.\nTry running me as admin?"));
                }
                ArrayList<File> failed = new ArrayList<>();
                int lowLim = Integer.parseInt(oldR.substring(0, (oldR.length() - 1) / 2));
                int upLim = Integer.parseInt(oldR.substring((oldR.length() + 1) / 2));
                int digits = Math.max(String.valueOf(upLim + shift).length(), oldR.substring((oldR.length() + 1) / 2).length());
                files.sort(AlphanumComparator);
                if (shift < 0)
                    files.remove(files.size() - 1);
                else {
                    Collections.reverse(files);
                    files.remove(0);
                }
                for (File f : files) {
                    int num = Integer.parseInt(f.getName().substring(0, f.getName().indexOf('.')));
                    String name = f.getName().substring(f.getName().indexOf('.'));
                    if (num <= upLim && num >= lowLim) {
                        num += shift;
                        name = num + name;
                        for (int i = 0; i < digits - String.valueOf(num).length(); i++)
                            name = "0".concat(name);
                        if (f.getName().equals(name)) continue;
                        if (f.renameTo(new File(name)))
                            count++;
                        else
                            failed.add(f);
                    }
                }
                if (count == 0 && failed.isEmpty()) {
                    System.out.println("YARE YARE DAZE.. The \"" + oldR + "\" range doesn't even exist!");
                } else {
                    System.out.printf("YATTA.. Renamed a total of %d files.%n", count);
                    if (!failed.isEmpty()) {
                        System.out.println("But i have failed to rename some files for some reason.");
                        System.out.println("You're a coder.. You'll figure it out.");
                        System.out.println("Have a nice day.");
                        System.out.println("List of files:");
                        for (File f : failed) {
                            System.out.println(f.getName());
                        }
                    }
                }
            }
        while (true) {
            int count = 0, title_i = 0;
            String first = "";
            String last = "";
            ArrayList<String> titles = null;
            System.out.println();
            System.out.println("Old name:");
            String oldName = scanner.nextLine();
            System.out.println("New name:");
            String newName = scanner.nextLine();
            ArrayList<File> files = new ArrayList<>(Arrays.asList((new File(".")).listFiles()));
            if (files == null) {
                throw new IOError(new IOException("HEH, This one wasn't my fault.\nTry running me as admin?"));
            }
            ArrayList<File> failed = new ArrayList<>();
            if (!oldName.contains("##") && newName.contains("##")) {
                try (Scanner reader = new Scanner(new File("Titles.txt"))) {
                    titles = new ArrayList<>();
                    while (reader.hasNext())
                        titles.add(reader.nextLine());
                } catch (FileNotFoundException e) {
                    System.out.println("Dude.. Want titles, provide titles. That's how life works.");
                    continue;
                }
            }
            if (oldName.contains("#") && newName.contains("#")) {
                files.sort(AlphanumComparator);
                for (int i = 0; i < files.size(); i++)
                    if (files.get(i).isDirectory() || !files.get(i).getName().matches(Pattern.quote(oldName).replaceFirst("##", "\\\\E.+\\\\Q").replaceFirst("#", "\\\\E\\\\d+(-\\\\d+)*\\\\Q")))
                        files.remove(i--);
                Pattern num_pattern = Pattern.compile("(?<=\\Q" + oldName.replaceFirst("##", "\\\\E.+\\\\Q").replaceFirst("#", "\\\\E)(\\\\d+(-\\\\d+)*)(?=\\\\Q") + "\\E)");
                Pattern title_pattern = null;
                if (oldName.contains("##") && newName.contains("##"))
                    title_pattern = Pattern.compile("(?<=\\Q" + oldName.replaceFirst("##", "\\\\E).+(?=\\\\Q").replaceFirst("#", "\\\\E(\\\\d+(-\\\\d+)*)\\\\Q") + "\\E)");
                Matcher m = num_pattern.matcher(files.get(files.size() - 1).getName());
                m.find();
                int digits = m.group().substring(m.group().lastIndexOf("-") + 1).length();
                for (File f : files) {
                    m = num_pattern.matcher(f.getName());
                    m.find();
                    String[] numarr = m.group().split("-");
                    String num = "", title;
                    if (oldName.contains("##") && newName.contains("##")) {
                        m = title_pattern.matcher(f.getName());
                        m.find();
                        title = m.group();
                    } else if (newName.contains("##"))
                        title = (title_i < titles.size()) ? titles.get(title_i++) : "";
                    else
                        title = "";
                    for (int i = 0; i < numarr.length; i++) {
                        for (int j = 0, k = numarr[i].length(); j < digits - k; j++)
                            numarr[i] = "0".concat(numarr[i]);
                        num = num.concat(numarr[i].concat("-"));
                    }
                    num = num.substring(0, num.length() - 1);
                    if (f.getName().equals(newName.replaceFirst("##", title).replaceFirst("#", num))) continue;
                    if (f.renameTo(new File(last = newName.replaceFirst("##", title).replaceFirst("#", num)))) {
                        if (count++ == 0) {
                            first = f.getName();
                        }
                    } else {
                        failed.add(f);
                    }
                }
                if (count == 0 && failed.isEmpty()) {
                    System.out.println("YARE YARE DAZE.. \"" + oldName + "\" doesn't even exist!");
                } else {
                    System.out.printf("YATTA.. Renamed a total of %d files, From \"%s\" to \"%s\".%n", count, first, last);
                    if (!failed.isEmpty()) {
                        System.out.println("But i have failed to rename some files for some reason.");
                        System.out.println("You're a coder.. You'll figure it out.");
                        System.out.println("Have a nice day.");
                        System.out.println("List of files:");
                        for (File f : failed) {
                            System.out.println(f.getName());
                        }
                    }
                }
            } else {
                if (new File(oldName).renameTo(new File(newName)))
                    System.out.println("MACBETH.. ONE FILE HAS BEEN RENAMED!");
                else
                    System.out.println("MEH.. I KNEW IT WOULDN'T WORK FROM THE BEGINNING ¯\\_(ツ)_/¯");
            }
        }
        //scanner.close();
    }
}