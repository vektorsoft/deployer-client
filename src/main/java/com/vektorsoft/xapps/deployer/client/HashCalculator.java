/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.xapps.deployer.client;

import com.vektorsoft.xapps.deployer.client.DeployerException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class HashCalculator {

	private static final String HASH_ALGORITHM = "SHA-1";
	private static final int DEFAULT_BUFFER_SIZE = 4096;

	/**
	 * Private constructor to prevent instantiation.
	 */
	private HashCalculator() {

	}

	public static String fileHash(File file) throws DeployerException {
		try {
			MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
			try(DigestInputStream dis = new DigestInputStream(new FileInputStream(file), digest)) {
				byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
				int count;
				while((count = dis.read(buffer)) > 0) {
					digest.update(buffer, 0, count);
				}
				return bytesToHex(digest.digest());
			} catch(IOException ex) {
				throw new DeployerException(ex);
			}
		} catch(NoSuchAlgorithmException ex) {
			throw new DeployerException(ex);
		}

	}

	private static String bytesToHex(byte[] data) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0;i < data.length;i++) {
			var hex = Integer.toHexString(0xff & data[i]);
			if(hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}
}
