package com.example.controller;

import com.example.utils.DownloadUtils;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.auth.COSSigner;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.region.Region;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @Author lqs2
 * @Description TODO
 * @Date 2018/6/28, Thu
 */
@Controller
@Repository
@Slf4j
@EnableTransactionManagement
public class CosController {

    @Value("${bucket1.name}")
    private String BUCKET_NAME1;
    @Value("${bucket60.name}")
    private String BUCKET_NAME60;
    @Value("${secret.id}")
    private String SECRET_ID;
    @Value("${secret.key}")
    private String SECRET_KEY;
    @Value("${bucket.region}")
    private String REGION;


    private static COSClient client;


    private COSClient getClient() {

        if (client == null) {
            COSCredentials cred = new BasicCOSCredentials(SECRET_ID, SECRET_KEY);
            ClientConfig clientConfig = new ClientConfig(new Region(REGION));
            client = new COSClient(cred, clientConfig);
        }
        return client;
    }

    @RequestMapping(value = "aaa", method = {RequestMethod.POST})
    @Transactional
    @ResponseBody
    public String String() {
        return SECRET_ID + ",." + SECRET_KEY;
    }

    @RequestMapping(value = "doDownload", method = {RequestMethod.GET})
    @Transactional
    @ResponseBody
    public void downloadFile(String bucketId, String filename, int file_save_days, HttpServletRequest request, HttpServletResponse response) {
        try {
            ServletContext context = request.getServletContext();
            String mimeType = context.getMimeType(bucketId);
            response.setContentType(mimeType);
//        2、设置下载的头信息

//        String newName = DownloadUtils.getName(request.getHeader("user-agent"), filename);

//        response.setHeader("content-disposition", "attachment;filename=" + newName);
            DownloadUtils.setFileDownloadHeader(response, filename);
            response.setCharacterEncoding("utf-8");

//        3、对拷流
            String bucketName = BUCKET_NAME1;
            if (file_save_days == 365) {
                bucketName = BUCKET_NAME60;
            }
            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, bucketId);
            COSObject cosObject = getClient().getObject(getObjectRequest);
            COSObjectInputStream ips = cosObject.getObjectContent();


            ServletOutputStream ops = response.getOutputStream();

//        FileOutputStream ops = new FileOutputStream(new File("/Users/lqs2/Desktop/1.txt"));
//            System.out.println(ips);
//            System.out.println(ops);

            IOUtils.copy(ips, ops);


        } catch (Exception e) {
            log.error(e.toString());
        }

//        int len ;
//        byte[] bytes = new byte[1024];
//        while ((len = ips.read(bytes))!= -1) {
//            System.out.println(len);
//            ops.write(bytes, 0, len);
//        }

//
//        String key = file.getFile_bucket_id();
//        GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(BUCKET_NAME, key, HttpMethodName.GET);
//
////        设置签名过期时间(可选), 若未进行设置, 则默认使用 ClientConfig 中的签名过期时间(5分钟)
////        设置签名在1个小时后过期
//        Date expirationDate = new Date(System.currentTimeMillis() + 60L * 60L * 1000L);
//        req.setExpiration(expirationDate);
//        URL downloadUrl = getClient().generatePresignedUrl(req);
////        String downloadUrlStr = downloadUrl.toString();
////        return file.getFile_bucket_id() +" "+ file.getFile_name();
//        System.out.println(downloadUrl.toString());
//        return downloadUrl.toString() + " " + file.getFile_name() + " " + file.getFile_bucket_id();
    }

    @RequestMapping(value = "initCosClient", method = {RequestMethod.GET})
    public String generateAuthStr() {
        return "sts.php";
    }


    @RequestMapping("generateUploadAuthorizationSig")
    public String generateAuthStr(String fileName) {
//        String bucketName = BUCKET_NAME;

//        System.out.println(BUCKET_NAME);
//        System.out.println(SECRET_ID);
//        System.out.println(SECRET_KEY);

        COSCredentials cred = new BasicCOSCredentials(SECRET_ID, SECRET_KEY);
        COSSigner signer = new COSSigner();
        //设置过期时间为 2 个小时
        Date expiredTime = new Date(System.currentTimeMillis() + 7200L * 1000L);
        // 要签名的 key, 生成的签名只能用于对应此 key 的上传
        return signer.buildAuthorizationStr(HttpMethodName.PUT, fileName, cred, expiredTime);
    }


    void rmFile(String bucket_id) {
        if (client == null) {
            COSCredentials cred = new BasicCOSCredentials("AKIDFKRetDGBkLzXu4iVt67zoTwpjw8OcQ9g", "TtAIF0Y7YM1UXcdXJtKdppdFf2DF4hEx");
            ClientConfig clientConfig = new ClientConfig(new Region("ap-shanghai"));
            client = new COSClient(cred, clientConfig);
        }
        try {
            client.deleteObject("public60-1253931949", bucket_id);
        } catch (Exception e) {
            log.error(e.toString());
        }
    }
}
