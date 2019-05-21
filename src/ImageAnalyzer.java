import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;

/**
 * The image analyzer class is designed to solve the following problem:
 * https://gist.github.com/ehmo/e736c827ca73d84581d812b3a27bb132
 */
public class ImageAnalyzer
{
    public static void main(String[] args)
    {
        try (Stream<String> imageURLs = Files.lines(Paths.get("data/input.txt")))
        {
            File outputFile = new File("output.csv");
            if (outputFile.createNewFile())
            {
                imageURLs.forEach(imageURL -> processImage(imageURL, outputFile.toPath()));
            }
            else
            {
                System.err.println("File already exists!");
            }
        }
        catch (IOException exception)
        {
            System.err.println(exception);
        }
    }

    /**
     * Process an image given a url to find the top
     * three common colors in that image.
     * @param imageURL a url pointing to an image
     * @param pathToOutputFile path to the file containing results
     */
    private static void processImage(String imageURL, Path pathToOutputFile)
    {
        try
        {
            BufferedImage image = ImageIO.read(new URL(imageURL));
            String commonColors = findCommonColors(image);
            String result = imageURL + "," + commonColors + "\n";

            Files.write(pathToOutputFile, result.getBytes(), StandardOpenOption.APPEND);
        }
        catch (IOException exception)
        {
            System.err.println(exception.toString() + " for image " + imageURL);
        }
    }

    /**
     * Returns a string that represents the top three colors
     * found in a given image.
     * @param image the image that needs to be processed
     * @return string representation of colors
     */
    private static String findCommonColors(BufferedImage image)
    {
        HashMap<Color, Integer> colorToCountMap = countColorsInImage(image);

        PriorityQueue<Map.Entry<Color, Integer>> sortedColors =
                new PriorityQueue<>(colorToCountMap.size(),
                        Comparator.comparingInt(
                                (ToIntFunction<Map.Entry<Color, Integer>>) Map.Entry::getValue).reversed());
        sortedColors.addAll(colorToCountMap.entrySet());

        Color firstColor = sortedColors.peek() != null ? sortedColors.poll().getKey() : null;
        Color secondColor = sortedColors.peek() != null ? sortedColors.poll().getKey() : null;
        Color thirdColor = sortedColors.peek() != null ? sortedColors.poll().getKey() : null;

        String firstColorHex = getHexFromColor(firstColor);
        String secondColorHex = getHexFromColor(secondColor);
        String thirdColorHex = getHexFromColor(thirdColor);

        return firstColorHex + "," + secondColorHex + "," + thirdColorHex;
    }

    /**
     * Creates a count of all the colors and their prevalence in the image.
     * @param image image that needs to be analyzed
     * @return map of colors and their integer counts
     */
    private static HashMap<Color, Integer> countColorsInImage(BufferedImage image)
    {
        HashMap<Color, Integer> colorToCountMap = new HashMap<>();
        for (int imageX = 0; imageX < image.getWidth(); imageX++)
        {
            for (int imageY = 0; imageY < image.getHeight(); imageY++)
            {
                Color color = new Color(image.getRGB(imageX, imageY));
                if (colorToCountMap.containsKey(color))
                {
                    colorToCountMap.put(color, colorToCountMap.get(color) + 1);
                }
                else
                {
                    colorToCountMap.put(color, 1);
                }
            }
        }
        return colorToCountMap;
    }

    /**
     * Convert a Color into a string representing its
     * hexadecimal format (#000000 - #FFFFFF).
     * @param color that needs to be converted
     * @return hexadecimal string representation
     */
    private static String getHexFromColor(Color color)
    {
        if (color == null)
        {
            return "NULL";
        }

        String rgbValue = Integer.toHexString(color.getRGB()).substring(2).toUpperCase();
        return "#" + rgbValue;
    }
}
