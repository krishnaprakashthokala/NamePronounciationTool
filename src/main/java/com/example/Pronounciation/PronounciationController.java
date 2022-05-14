package com.example.Pronounciation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.WritableResource;
        import org.springframework.util.StreamUtils;
        import org.springframework.web.bind.annotation.*;

        import java.io.IOException;
        import java.io.OutputStream;
        import java.nio.charset.Charset;

@RestController
@RequestMapping("blob")
public class PronounciationController {

  @Value("azure-blob://krishnacontainer/sample-mp4-file-small.mp4")
    //@Value("azure-blob:")

    private Resource blobFile;
    @Autowired
    private PronounciationService service;

    @GetMapping("/readBlobFile/{id}")
    public String readBlobFile(@PathVariable String id) throws IOException {
       Resource blobFile = service.getBlobFile(id);
       System.out.println("  blobFile  " +         blobFile.getFile()
       );
        return StreamUtils.copyToString(
                blobFile.getInputStream(),
               // blobFile.getInputStream(),

                Charset.defaultCharset());
    }


    @GetMapping("/readBlobFile")
    public String readBlobFile() throws IOException {
       // Resource blobFile = service.getBlobFile(id);
        System.out.println("  blobFile  " + blobFile.getURL());
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        return StreamUtils.copyToString(
               this.blobFile.getInputStream(),
              //  resourceLoader.getResource(blobFile.getURL()+"sample-mp4-file-small.mp4").getInputStream(),

                Charset.defaultCharset());
    }

    @PostMapping("/writeBlobFile")
    public String writeBlobFile(@RequestBody String data) throws IOException {
        Resource blobFile = service.getBlobFile(data);

        try (OutputStream os = ((WritableResource) blobFile).getOutputStream()) {
            os.write(data.getBytes());
        }
        return "file was updated";
    }
    @GetMapping("/download")
    public String download(@RequestBody PronounciationToolDataReq req) throws IOException {
      return service.downloadFile(req.getId());
      //  return StreamUtils.copyToString(
                //this.blobFile.getInputStream(),
              //  blobFile.getInputStream(),

             //   Charset.defaultCharset());
    }

    @PutMapping("/uploadFile")
    public void uploadFile(@RequestBody PronounciationToolDataReq req)  throws IOException {
        service.uploadFile(req.getId(), req.getCustomInput());
        //  return StreamUtils.copyToString(
        //this.blobFile.getInputStream(),
        //  blobFile.getInputStream(),

        //   Charset.defaultCharset());
    }

}