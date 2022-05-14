package com.example.Pronounciation;
import com.azure.core.http.ProxyOptions;
import com.azure.core.http.netty.NettyAsyncHttpClientBuilder;
import com.azure.core.util.BinaryData;
import com.azure.core.util.Context;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.identity.ManagedIdentityCredential;
import com.azure.identity.ManagedIdentityCredentialBuilder;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobRequestConditions;
import com.azure.storage.blob.options.BlobParallelUploadOptions;
import com.azure.storage.blob.sas.BlobContainerSasPermission;
import com.azure.storage.blob.sas.*;
import com.azure.storage.common.sas.AccountSasPermission;
import com.azure.storage.common.sas.AccountSasService;
import com.azure.storage.common.sas.*;
import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.blob.*;
import com.microsoft.windowsazure.serviceruntime.RoleEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.time.OffsetDateTime;
@Service
public class PronounciationService {
    public static final String storageConnectionString =
            "DefaultEndpointsProtocol=http;" +
                    "AccountName=krishnau743622;" +
                    "AccountKey=75vP5cmd2vTGdjTztXNNOTwAoonh+QPJ5IsqM6tD8/uwD1tpQK18vj6wfDKCvTk8mE4zNtgalTP1+ASt8+RqDQ==";
    public void createContainer(){
        String storageConnectionString1 =
                RoleEnvironment.getConfigurationSettings().get("StorageConnectionString");
        try
        {
            // Retrieve storage account from connection-string.
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString1);

            // Create the blob client.
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

            // Get a reference to a container.
            // The container name must be lower case
            CloudBlobContainer container = blobClient.getContainerReference("krishnacontainer");

            // Create the container if it does not exist.
            container.createIfNotExists();
        }
        catch (Exception e)
        {
            // Output the stack trace.
            e.printStackTrace();
        }
    }

    public String createSasToken(){
        ManagedIdentityCredential managedIdentityCredential = new ManagedIdentityCredentialBuilder()
                .clientId("<USER ASSIGNED MANAGED IDENTITY CLIENT ID>")

                .build();

        BlobServiceClient blobStorageClient = new BlobServiceClientBuilder()
                .endpoint("<your-storage-account-url>")
                .credential(new DefaultAzureCredentialBuilder().build())
                .buildClient();
        ProxyOptions options = new ProxyOptions(ProxyOptions.Type.HTTP, new InetSocketAddress("localhost", 888));
        BlobServiceClient client = new BlobServiceClientBuilder()
                .httpClient(new NettyAsyncHttpClientBuilder().proxy(options).build())
                .buildClient();

        OffsetDateTime expiryTime = OffsetDateTime.now().plusDays(1);
        AccountSasPermission accountSasPermission = new AccountSasPermission().setReadPermission(true);
        AccountSasService services = new AccountSasService().setBlobAccess(true);
        AccountSasResourceType resourceTypes = new AccountSasResourceType().setObject(true);

// Generate the account sas.
       AccountSasSignatureValues accountSasValues =
                new AccountSasSignatureValues(expiryTime, accountSasPermission, services, resourceTypes);
        String sasToken = client.generateAccountSas(accountSasValues);
return sasToken;
// Generate a sas using a container client
       /* BlobContainerSasPermission containerSasPermission = new BlobContainerSasPermission().setCreatePermission(true);
        BlobServiceSasSignatureValues serviceSasValues =
                new BlobServiceSasSignatureValues(expiryTime, containerSasPermission);
        blobContainerClient.generateSas(serviceSasValues);
*/
// Generate a sas using a blob client
      /*  BlobSasPermission blobSasPermission = new BlobSasPermission().setReadPermission(true);
        serviceSasValues = new BlobServiceSasSignatureValues(expiryTime, blobSasPermission);
        blobClient.generateSas(serviceSasValues);*/
    }

    public void downloadBlob(){
          String storageConnectionString2 =
                "DefaultEndpointsProtocol=https;" +

                        "AccountName=krishnau743622;" +
                        "AccountKey=75vP5cmd2vTGdjTztXNNOTwAoonh+QPJ5IsqM6tD8/uwD1tpQK18vj6wfDKCvTk8mE4zNtgalTP1+ASt8+RqDQ==";
        try
        {
            System.out.println("string file " +storageConnectionString2);
            String storageConnectionString1 =
                    RoleEnvironment.getConfigurationSettings().get(storageConnectionString2);
            // Retrieve storage account from connection-string.
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString1);

            // Create the blob client.
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

            // Retrieve reference to a previously created container.
            CloudBlobContainer container = blobClient.getContainerReference("krishnacontainer");

            // Loop through each blob item in the container.
            for (ListBlobItem blobItem : container.listBlobs()) {
                // If the item is a blob, not a virtual directory.
                if (blobItem instanceof CloudBlob) {
                    // Download the item and save it to a file with the same name.
                    CloudBlob blob = (CloudBlob) blobItem;
                    blob.download(new FileOutputStream("C:\\Users\\noema\\Downloads\\" + blob.getName()));
                }
            }
        }
        catch (Exception e)
        {
            // Output the stack trace.
            e.printStackTrace();
        }
    }
    public Resource getBlobFile(String UID){
        Resource file = null;
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        switch(UID){
            case "u743622":
             file = resourceLoader.getResource("azure-blob://krishnacontainer/sample-mp4-file-small.mp4");
             break;
            case "u743623":
                file = resourceLoader.getResource("https://vishnuaccount1.blob.core.windows.net//vishnucontainer/sample-mp4-file-small.mp4");
                break;

        }
        return file;
    }

    public String downloadFile(String id) throws IOException {
        String str = null;
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .endpoint("https://krishnau743622.blob.core.windows.net/")
                .sasToken("?sv=2020-08-04&ss=bfqt&srt=sco&sp=rwdlacupitfx&se=2022-08-30T22:04:58Z&st=2022-05-14T14:04:58Z&spr=https&sig=rXIUPqY4ECwJlBQa8p4OLvgs6BoGqC%2FcafnlBVT%2FqwM%3D")
                .buildClient();
        BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient("krishnacontainer");
       // BlobClient blobClient = blobContainerClient.getBlobClient("sample-mp4-file-small.mp4");
         BlobClient blobClient = blobContainerClient.getBlobClient(id);

        //  System.out.println(" blobclient  "  + blobClient.downloadContent());
      /*  try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            blobClient.downloadStream(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        str = StreamUtils.copyToString(
                blobClient.downloadContent().toStream(),
                // blobFile.getInputStream(),

                Charset.defaultCharset());
        //System.out.println(" Data File    "  + str);
        return str;
    }


    public void uploadFile(String myblock, String data) throws IOException {
        String str = null;
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .endpoint("https://krishnau743622.blob.core.windows.net/")
                .sasToken("?sv=2020-08-04&ss=bfqt&srt=sco&sp=rwdlacupitfx&se=2022-08-30T22:04:58Z&st=2022-05-14T14:04:58Z&spr=https&sig=rXIUPqY4ECwJlBQa8p4OLvgs6BoGqC%2FcafnlBVT%2FqwM%3D")
                .buildClient();
        BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient("krishnacontainer");
        BlobClient blobClient = blobContainerClient.getBlobClient(myblock);
        //  System.out.println(" blobclient  "  + blobClient.downloadContent());
      /*  try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            blobClient.downloadStream(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        String dataSample = data;
       // blobClient.upload(BinaryData.fromString(dataSample));

        try (ByteArrayInputStream dataStream = new ByteArrayInputStream(dataSample.getBytes())) {
            blobClient.upload(dataStream, dataSample.length(), true /* overwrite */);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (ByteArrayInputStream dataStream = new ByteArrayInputStream(dataSample.getBytes())) {
            BlobParallelUploadOptions options =
                    new BlobParallelUploadOptions(dataStream, dataSample.length());
            // Setting IfMatch="*" ensures the upload will succeed only if there is already a blob at the destination.
            options.setRequestConditions(new BlobRequestConditions().setIfMatch("*"));
            blobClient.uploadWithResponse(options, null, Context.NONE);
        } catch (IOException e) {
            e.printStackTrace();
        }
       // blobClient.upload(BinaryData.fromString(dataSample));
        str = StreamUtils.copyToString(
                blobClient.downloadContent().toStream(),
                // blobFile.getInputStream(),

                Charset.defaultCharset());
        System.out.println(" Data File    "  + str);

    }
}
