package com.example.vedioadvertisment;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.nio.file.*;

@WebServlet("/segment")
public class SegmentServlet extends HttpServlet {

    private String baseDir;

    @Override
    public void init() {
        baseDir = getServletContext().getInitParameter("baseDir");
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String file = req.getParameter("file");
        File ts = new File(baseDir + "/" + file);

        resp.setContentType("video/MP2T");
        resp.setHeader("Cache-Control", "no-cache");

        FileInputStream fis = new FileInputStream(ts);
        OutputStream os = resp.getOutputStream();

        byte[] buf = new byte[8192];
        int len;
        while((len = fis.read(buf)) != -1) {
            os.write(buf, 0, len);
        }
        fis.close();
    }
}