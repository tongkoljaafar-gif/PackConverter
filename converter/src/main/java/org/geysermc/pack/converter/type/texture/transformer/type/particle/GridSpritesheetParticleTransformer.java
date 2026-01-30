/*
 * Copyright (c) 2026 GeyserMC. http://geysermc.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 *
 *  @author GeyserMC
 *  @link https://github.com/GeyserMC/PackConverter
 *
 */

package org.geysermc.pack.converter.type.texture.transformer.type.particle;

import net.kyori.adventure.key.Key;
import org.geysermc.pack.converter.type.texture.transformer.TextureTransformer;
import org.geysermc.pack.converter.type.texture.transformer.TransformContext;
import org.geysermc.pack.converter.util.KeyUtil;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.texture.Texture;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public abstract class GridSpritesheetParticleTransformer implements TextureTransformer {
    private final String[] javaPaths;
    private final String bedrockPath;
    private final int rows;
    private final int columns;
    private final int particleWidth;
    private final int particleHeight;

    public GridSpritesheetParticleTransformer(String bedrockPath, int rows, int columns, int particleWidth, int particleHeight, String... javaPaths) {
        this.javaPaths = javaPaths;
        this.bedrockPath = bedrockPath;
        this.rows = rows;
        this.columns = columns;
        this.particleWidth = particleWidth;
        this.particleHeight = particleHeight;
    }

    @Override
    public void transform(@NotNull TransformContext context) throws IOException {
        BufferedImage image = new BufferedImage(columns * particleWidth, rows * particleHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();

        context.debug(String.format("Creating particle spritesheet %s", this.bedrockPath));

        int k = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                Texture texture = context.pollOrPeekVanilla(KeyUtil.key(Key.MINECRAFT_NAMESPACE, javaPaths[k]));
                if (texture == null) {
                    context.warn("%s is missing. Please report this.".formatted(javaPaths[k]));
                    continue;
                }

                BufferedImage javaImage = this.readImage(texture);

                g.drawImage(javaImage, j * particleWidth, i * particleHeight, null);
                k++;
            }
        }

        context.offer(KeyUtil.key(Key.MINECRAFT_NAMESPACE, this.bedrockPath), image, "png");
    }
}
