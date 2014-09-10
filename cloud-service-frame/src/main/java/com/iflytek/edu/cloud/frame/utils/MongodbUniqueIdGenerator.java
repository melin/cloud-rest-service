/**
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.iflytek.edu.cloud.frame.utils;

import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 详细描述请参考：http://www.cnblogs.com/xjk15082/archive/2011/09/18/2180792.html
 * 
 * @author libinsong1204@gmail.com
 * @date 2012-3-2 上午10:05:38
 */
public class MongodbUniqueIdGenerator {
	static final Logger LOGGER = LoggerFactory.getLogger(MongodbUniqueIdGenerator.class);

	private static AtomicInteger _nextInc = new AtomicInteger(
			(new java.util.Random()).nextInt());

	private MongodbUniqueIdGenerator() {
	}

	public static String getUniqueId() {
		int _time = (int) (System.currentTimeMillis() / 1000);
		int _machine = _genmachine;
		int _inc = _nextInc.getAndIncrement();

		byte b[] = new byte[12];
		ByteBuffer bb = ByteBuffer.wrap(b);
		// by default BB is big endian like we need
		bb.putInt(_time);
		bb.putInt(_machine);
		bb.putInt(_inc);

		StringBuilder buf = new StringBuilder(24);

		for (int i = 0; i < b.length; i++) {
			int x = b[i] & 0xFF;
			String s = Integer.toHexString(x);
			if (s.length() == 1)
				buf.append("0");
			buf.append(s);
		}

		return buf.toString();
	}

	private static final int _genmachine;
	static {

		try {
			// build a 2-byte machine piece based on NICs info
			final int machinePiece;
			{
				StringBuilder sb = new StringBuilder();
				Enumeration<NetworkInterface> e = NetworkInterface
						.getNetworkInterfaces();
				while (e.hasMoreElements()) {
					NetworkInterface ni = e.nextElement();
					sb.append(ni.toString());
				}
				machinePiece = sb.toString().hashCode() << 16;
				LOGGER.info("machine piece post: "
						+ Integer.toHexString(machinePiece));
			}

			// add a 2 byte process piece. It must represent not only the JVM
			// but the class loader.
			// Since static var belong to class loader there could be collisions
			// otherwise
			final int processPiece;
			{
				int processId = new java.util.Random().nextInt();
				try {
					processId = java.lang.management.ManagementFactory
							.getRuntimeMXBean().getName().hashCode();
				} catch (Throwable t) {
					//emtry
				}

				ClassLoader loader = MongodbUniqueIdGenerator.class.getClassLoader();
				int loaderId = loader != null ? System.identityHashCode(loader)
						: 0;

				StringBuilder sb = new StringBuilder();
				sb.append(Integer.toHexString(processId));
				sb.append(Integer.toHexString(loaderId));
				processPiece = sb.toString().hashCode() & 0xFFFF;
				LOGGER.info("process piece: "
						+ Integer.toHexString(processPiece));
			}

			_genmachine = machinePiece | processPiece;
			LOGGER.info("machine : " + Integer.toHexString(_genmachine));
		} catch (java.io.IOException ioe) {
			throw new RuntimeException(ioe);
		}

	}
	
	public static void main(String[] args) {
		System.out.println(MongodbUniqueIdGenerator.getUniqueId());
		System.out.println(MongodbUniqueIdGenerator.getUniqueId());
		System.out.println(MongodbUniqueIdGenerator.getUniqueId());
	}
}
