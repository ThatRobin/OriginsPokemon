package io.github.thatrobin.cobblemorigins.utils;

import com.github.oscar0812.pokeapi.models.pokemon.Pokemon;
import com.github.oscar0812.pokeapi.models.pokemon.PokemonSpecies;
import com.github.oscar0812.pokeapi.utils.Client;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PokemonAssetProvider implements DataProvider {

    protected final FabricDataOutput dataOutput;

    protected PokemonAssetProvider(FabricDataOutput dataOutput) {
        this.dataOutput = dataOutput;

    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        final List<CompletableFuture<?>> futures = new ArrayList<>();
        for (PokemonSpecies pokemonSpecies : Client.getGenerationById(1).getPokemonSpecies()) {
            Pokemon pokemon = Pokemon.getByName(pokemonSpecies.getName());
            Path texturePath = this.dataOutput.getResolver(DataOutput.OutputType.RESOURCE_PACK, "textures/item").resolve(new Identifier(dataOutput.getModId(), pokemonSpecies.getName()), "png");
            Path modelPath = this.dataOutput.getResolver(DataOutput.OutputType.RESOURCE_PACK, "models/item").resolveJson(new Identifier(dataOutput.getModId(), pokemonSpecies.getName()));
            futures.add(downloadImage(pokemon.getSprites().getFrontDefault(), writer, texturePath));

            JsonObject modelJson = new JsonObject();
            JsonObject textureJson = new JsonObject();
            modelJson.addProperty("parent", "minecraft:item/generated");
            textureJson.addProperty("layer0", "cobblemorigins:item/"+pokemonSpecies.getName());
            modelJson.add("textures", textureJson);

            futures.add(DataProvider.writeToPath(writer, modelJson, modelPath));
        }
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    public static CompletableFuture<Void> downloadImage(String imageUrl, DataWriter writer, Path path) {
        return CompletableFuture.runAsync(() -> {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                HashingOutputStream hashingOutputStream = new HashingOutputStream(Hashing.sha1(), byteArrayOutputStream);
                URL url = new URL(imageUrl);
                try (InputStream in = url.openStream()) {
                    BufferedImage originalImage = ImageIO.read(in);

                    // Trim image of transparency
                    BufferedImage trimmedImage = trim(originalImage);

                    int squareSize = Math.max(trimmedImage.getWidth(), trimmedImage.getHeight());
                    BufferedImage squareImage = makeSquare(trimmedImage, squareSize);

                    // Convert the trimmed image to byte array
                    ByteArrayOutputStream trimmedImageStream = new ByteArrayOutputStream();
                    ImageIO.write(squareImage, "png", trimmedImageStream);
                    writer.write(path, trimmedImageStream.toByteArray(), hashingOutputStream.hash());
                }
            } catch (IOException e) {
                throw new RuntimeException("Error downloading image: " + e.getMessage(), e);
            }
        }, Util.getMainWorkerExecutor());
    }

    private static BufferedImage trim(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        int top = height / 2;
        int bottom = top;
        int left = width / 2 ;
        int right = left;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (isFg(img.getRGB(x, y))){
                    top    = Math.min(top, y);
                    bottom = Math.max(bottom, y);
                    left   = Math.min(left, x);
                    right  = Math.max(right, x);
                }
            }
        }

        return img.getSubimage(left, top, right - left, bottom - top);
    }

    private static BufferedImage makeSquare(BufferedImage originalImage, int squareSize) {
        BufferedImage squareImage = new BufferedImage(squareSize, squareSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = squareImage.createGraphics();

        // Fill with a background color (white in this example, you can change it)
        squareImage = g.getDeviceConfiguration().createCompatibleImage(squareSize, squareSize, Transparency.TRANSLUCENT);
        g = squareImage.createGraphics();
        g.setColor(new Color(0, 0, 0, 0));
        g.fillRect(0, 0, squareSize, squareSize);


        // Draw the trimmed image onto the square image, centered
        int x = (squareSize - originalImage.getWidth()) / 2;
        int y = (squareSize - originalImage.getHeight()) / 2;
        g.drawImage(originalImage, x, y, null);
        g.dispose();

        return squareImage;
    }

    private static boolean isFg(int v) {
        Color c = new Color(v);
        return(isColor((c.getRed() + c.getGreen() + c.getBlue())/2));
    }

    private static boolean isColor(int c) {
        return c > 0 && c < 255;
    }

    @Override
    public String getName() {
        return "Pokemon Asset Gen";
    }
}
