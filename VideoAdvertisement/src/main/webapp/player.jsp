<video id="player" controls width="800"></video>
<script src="https://cdn.jsdelivr.net/npm/hls.js@latest"></script>

<script>
    let url = "playlist?video=<%=request.getParameter("video")%>&ad=<%=request.getParameter("ad")%>";

    if(Hls.isSupported()){
        let hls = new Hls();
        hls.loadSource(url);
        hls.attachMedia(document.getElementById("player"));
    }
</script>
