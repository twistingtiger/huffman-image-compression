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

package app.utils;

import com.sun.deploy.util.StringUtils;

import java.awt.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;

/**
 * A collection of utility functions that are used throughout the application.
 */

public class Utils
{
    /**
     * Checks if the file extension in a file matches with the one specified.
     *
     * @param fileExtension The specified file extension the file must have.
     * @param filePath          The file to be checked.
     * @return              true if the file has the specified file extension,
     *                      false otherwise.
     */
    public static boolean isFileExtensionValid(String fileExtension, String filePath)
    {
        String upperCaseExt = fileExtension.toUpperCase();
        String lowerCaseExt = fileExtension.toLowerCase();
        String extension = filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length());

        return extension.equals(upperCaseExt) || extension.equals(lowerCaseExt);
    }

    /**
     * Convert a <tt>Color</tt> object into its RGBA integer value and return said value.
     *
     * @param  c Color object to be converted to RGBA.
     * @return   The RGBA integer value of the specified Color object.
     * @see      Color
     */
    public static int colorToRGBA(Color c)
    {
        return ((c.getRed() << 24) & 0xFF000000) | ((c.getGreen() << 16) & 0x00FF0000) |
               ((c.getBlue() << 8) & 0x0000FF00) | (c.getAlpha() & 0x000000FF);
    }
}
