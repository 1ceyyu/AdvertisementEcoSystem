package com.example.vedioadvertisment;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.*;

@WebServlet("/playlist")
public class PlaylistServlet extends HttpServlet {

    private AdManager adManager;

    @Override
    public void init() {
        String baseDir = getServletContext().getInitParameter("baseDir");
        adManager = new AdManager(baseDir);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String video = req.getParameter("video");
        String ad = req.getParameter("ad");

        resp.setContentType("application/vnd.apple.mpegurl");
        PrintWriter out = resp.getWriter();

        try {
            List<String> list = adManager.build(video, ad);

            out.println("#EXTM3U");

            for (String line : list) {
                String[] arr = line.split(" ");
                out.println("#EXTINF:" + arr[1] + ",");
                out.println(req.getContextPath() + "/segment?file=" + arr[0]);
            }

            out.println("#EXT-X-ENDLIST");

        } catch (Exception e) {
            e.printStackTrace();
            out.println("#ERROR");
        }
    }
}