/*
 * Huffman Image Compression
 * Copyright (C) 2017  Sean Ballais, Kenn Pulma
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package app.modules;

import app.utils.Utils;
import app.utils.ds.HuffmanDistribution;
import app.utils.ds.HuffmanNode;
import app.utils.ds.HuffmanTree;
import app.utils.enums.Movement;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.*;

/**
 * The decompressor is responsible for converting a compressed image using a Huffman Tree to
 * a BufferedImage that can be used displayed in a Java program.
 *
 * The decompressor only needs to have a tree that is required to decode the bits in the compressed
 * file to the appropriate color of a specific pixel. The decompressor requires that the first
 * eight bytes of the compressed image to contain the dimensions of the original image. The width
 * should be stored in the first four bytes, and the height should be stored in the remaining four
 * bytes of the eight bytes.
 *
 * @see Compressor
 */

public class Decompressor
{
    private HuffmanTree tree;
    private boolean hasReachedMaxPixel;

    /**
     * Constructs a new <tt>Decompressor</tt> object with an empty Huffman tree.
     */
    public Decompressor()
    {
        this.tree = new HuffmanTree();
        hasReachedMaxPixel = false;
    }

    /**
     * Generate a Huffman tree based on a Huffman distribution that is usually
     * generated by a Huffman Trainer.
     *
     * @param distribution
     *        A <tt>HuffmanDistribution</tt> object that contains the
     *        color values of an image or a set of images and their
     *        respective frequencies.
     * @see   HuffmanDistribution
     */
    public void generateTree(HuffmanDistribution distribution)
    {
        tree.generateTree(distribution);
    }

    /**
     * Decompress the compressed image and return a <tt>BufferedImage</tt> containing
     * the pixel values of the decompressed image.
     *
     * @param  sourceFile  The compressed image.
     * @return             a <tt>BufferedImage</tt> containing the pixel values of
     *                     the decompressed image.
     * @throws IOException if something went wrong while reading the file, or it does not exist.
     */
    public BufferedImage decompress(String sourceFile) throws IOException
    {
        if (!Utils.isFileExtensionValid("pnb", sourceFile)) {
            throw new IOException("File extension should be '.pnb'.");
        }

        Path sourcePath = Paths.get(sourceFile);
        byte[] imageBytes = Files.readAllBytes(sourcePath);

        int width = getDimension(imageBytes, 0);
        int height = getDimension(imageBytes, 4);
        BufferedImage resultingImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // Yeah, splicing will add an additional linear time to this decompression algorithm
        // (though it'll only increment this algorithm's complexity coefficient by one).
        // Soooooooooooooooo, we ain't gonna do that.

        applyColors(resultingImage, imageBytes, width, height);
        return resultingImage;
    }

    private void applyColors(BufferedImage resultingImage, byte[] imageBytes, int width, int height)
    {
        hasReachedMaxPixel = false;
        Point currentPoint = new Point(0, 0);
        for (int i = 8; i < imageBytes.length; i++) {
            processByte(resultingImage, imageBytes[i], currentPoint, width, height);

            if (hasReachedMaxPixel) {
                break;
            }
        }
    }

    private void processByte(BufferedImage resultingImage, byte imageByte, Point currentPoint, int width, int height)
    {
        for (int j = 7; j >= 0; j--) {
            byte coding = (byte) ((imageByte >> j) & 1);
            Movement movement = (coding == 0) ? Movement.LEFT : Movement.RIGHT;
            HuffmanNode currentNode = tree.traverseTree(movement);

            if (currentNode.isALeaf()) {
                processLeaf(resultingImage, currentNode, currentPoint, width, height);

                if (currentPoint.x == width - 1 && currentPoint.y == height - 1) {
                    hasReachedMaxPixel = true;
                    break;
                }
            }
        }
    }

    private void processLeaf(BufferedImage resultingImage, HuffmanNode currentNode, Point currentPoint, int width, int height)
    {
        int colorValue = RGBAToARGB(currentNode.getColorValue());
        //System.out.println("Current color: " + colorValue);
        //System.out.println("Current point: (" + currentPoint.x + ", " + currentPoint.y + ")");
        resultingImage.setRGB(currentPoint.x, currentPoint.y, colorValue);

        currentPoint.setLocation(++currentPoint.x, currentPoint.y);
        if (currentPoint.x == width) { currentPoint.setLocation(0, ++currentPoint.y); }

        //System.out.println("Current point (modified): (" + currentPoint.x + ", " + currentPoint.y + ")");
    }

    private int RGBAToARGB(int colorValue)
    {
        return ((colorValue >> 8) & 0x00FFFFFF) | ((colorValue << 24) & 0xFF000000);
    }

    private int getDimension(byte[] data, int start)
    {
        return ((data[start] & 0xFF) << 24) | ((data[start + 1] & 0xFF) << 16) |
               ((data[start + 2] & 0xFF) << 8) | (data[start + 3] & 0xFF);
    }
}
