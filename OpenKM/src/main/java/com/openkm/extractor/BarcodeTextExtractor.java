/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2014 Paco Avila & Josep Llort
 * 
 * No bytes were intentionally harmed during the development of this application.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.extractor;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.extractor.AbstractTextExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.GenericMultipleBarcodeReader;
import com.google.zxing.multi.MultipleBarcodeReader;

/**
 * Text extractor for image documents.
 * Use OCR from https://code.launchpad.net/cuneiform-linux
 */
public class BarcodeTextExtractor extends AbstractTextExtractor {
	
	/**
	 * Logger instance.
	 */
	private static final Logger log = LoggerFactory.getLogger(BarcodeTextExtractor.class);
	
	/**
	 * Creates a new <code>TextExtractor</code> instance.
	 */
	public BarcodeTextExtractor() {
		super(new String[] { "image/tiff", "image/gif", "image/jpeg", "image/png" });
	}
	
	/**
	 * Use in AbbyTextExtractor subclass
	 */
	public BarcodeTextExtractor(String[] contentTypes) {
		super(contentTypes);
	}
	
	// -------------------------------------------------------< TextExtractor >
	
	/**
	 * {@inheritDoc}
	 */
	public Reader extractText(InputStream stream, String type, String encoding) throws IOException {
		try {
			BufferedImage image = ImageIO.read(stream);
			String text = multiple(image);
			return new StringReader(text);
		} catch (Exception e) {
			log.warn("Failed to extract barcode text", e);
			return new StringReader("");
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}
	
	/**
	 * Decode only one barcode
	 */
	@SuppressWarnings("unused")
	private String simple(BufferedImage img) throws NotFoundException, ChecksumException, FormatException {
		long begin = System.currentTimeMillis();
		LuminanceSource source = new BufferedImageLuminanceSource(img);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		com.google.zxing.Reader reader = new MultiFormatReader();
		Result result = reader.decode(bitmap);
		
		log.trace("simple.Time: {}", System.currentTimeMillis() - begin);
		return result.getText();
	}
	
	/**
	 * Decode all barcodes in the image
	 */
	private String multiple(BufferedImage img) throws NotFoundException, ChecksumException, FormatException {
		long begin = System.currentTimeMillis();
		LuminanceSource source = new BufferedImageLuminanceSource(img);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		com.google.zxing.Reader reader = new MultiFormatReader();
		MultipleBarcodeReader bcReader = new GenericMultipleBarcodeReader(reader);
		Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
		hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
		StringBuilder sb = new StringBuilder();
		
		for (Result result : bcReader.decodeMultiple(bitmap, hints)) {
			sb.append(result.getText()).append(" ");
		}
		
		log.trace("multiple.Time: {}", System.currentTimeMillis() - begin);
		return sb.toString();
	}
}
