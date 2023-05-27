package com.mxy.regi.controller;

import com.mxy.regi.common.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {
    @Value("${regi.path}")
    private String basepath;
    /**
     * 上传文件
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public JsonResult<String> upload(MultipartFile file) {
        log.info(file.toString());

        //原始文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用UUID重新生成文件名，防止名字重复冲突
        String fileName = UUID.randomUUID().toString() + suffix ;

        File dir = new File(basepath);
        if(!dir.exists()){
            //创建新目录
            dir.mkdirs();
        }
        try {
            file.transferTo(new File(basepath+fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JsonResult.success(fileName);
    }

    @GetMapping("/download")
    public void download(String name , HttpServletResponse response){
        try{
            //输入流，通过输入流读取内容
            FileInputStream fileInputStream = new FileInputStream(new File(basepath+name));
            //输出流，将文件写回浏览器，在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");

            int len = 0 ;
            byte[] bytes = new byte[1024];
            while((len = fileInputStream.read(bytes))!=-1){

                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            outputStream.close();
            fileInputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
