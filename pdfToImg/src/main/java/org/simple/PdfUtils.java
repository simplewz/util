package org.simple;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

public class PdfUtils {
	
	/**
	 * 将pdf文件转换为多张图片，pdf文件有多少页就转换为多少张图片，图片文件的存放路径与该pdf文件所在的目录同级
	 * @param  pdfFilePath  pdf文件地址
	 * @return 返回转换后的图片路径集合
	 */
	public List<String> pdf2MutiImagePath(String pdfFilePath){
		
		List<String> imagePathList=new ArrayList<String>();
		
		String fileDirectory = pdfFilePath.substring(0,pdfFilePath.lastIndexOf("."));//��ȡȥ����׺���ļ�·��
		 
        String imagePath;
        File file = new File(pdfFilePath);
        try {
            File f = new File(fileDirectory);
            if(!f.exists()){
                f.mkdir();
            }
            PDDocument doc = PDDocument.load(file);
            //doc=PDDocument.load(new FileInputStream(""));
            PDFRenderer renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();
            for(int i=0; i<pageCount; i++){
            	// 方式1,第二个参数是设置缩放比(即像素)
                // BufferedImage image = renderer.renderImageWithDPI(i, 296);
                // 方式2,第二个参数是设置缩放比(即像素)
                BufferedImage image = renderer.renderImage(i, 2f);  //第二个参数越大生成图片分辨率越高，转换时间也就越长
                imagePath = fileDirectory + "/"+i + ".jpg";
                ImageIO.write(image, "PNG", new File(imagePath));               
                
                imagePathList.add(imagePath);
            }
            doc.close();              ///关闭文件,不然该pdf文件会一直被占用。
        } catch (IOException e) {
            e.printStackTrace();
        }		
		
		return imagePathList;
	}
	
	public String pdf2OneImpage(String pdfFilePath) {
		String fileDirectory = pdfFilePath.substring(0,pdfFilePath.lastIndexOf("."));//获取去除后缀的文件路径
        String imagePath=fileDirectory+".jpg";
        
        List<BufferedImage> picList=new ArrayList<BufferedImage>();
        File file = new File(pdfFilePath);
        try {
            File f = new File(fileDirectory);
            if(!f.exists()){
                f.mkdir();
            }
            PDDocument doc = PDDocument.load(file);
            PDFRenderer renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();
            for(int i=0; i<pageCount; i++){
                BufferedImage image = renderer.renderImage(i, 2f);  //第二个参数越大生成图片分辨率越高，转换时间也就越长
                picList.add(image);                                   
            }
            doc.close();              //关闭文件,不然该pdf文件会一直被占用。
        } catch (IOException e) {
            e.printStackTrace();
        }		
		
        yPic(picList, imagePath);
        
        return imagePath;
		
	}
	
	/**
     * 将宽度相同的图片，竖向追加在一起 ##注意：宽度必须相同
     *
     * @param piclist 文件流数组
     * @param outPath 输出路径
     */
    public static void yPic(List<BufferedImage> piclist, String outPath) {// 纵向处理图片
        if (piclist == null || piclist.size() <= 0) {
            System.out.println("图片数组为空!");
            return;
        }
        try {
            int height = 0, // 总高度
                    width = 0, // 总宽度
                    _height = 0, // 临时的高度 , 或保存偏移高度
                    __height = 0, // 临时的高度，主要保存每个高度
                    picNum = piclist.size();// 图片的数量
            int[] heightArray = new int[picNum]; // 保存每个文件的高度
            BufferedImage buffer = null; // 保存图片流
            List<int[]> imgRGB = new ArrayList<int[]>(); // 保存所有的图片的RGB
            int[] _imgRGB; // 保存一张图片中的RGB数据
            for (int i = 0; i < picNum; i++) {
                buffer = piclist.get(i);
                heightArray[i] = _height = buffer.getHeight();// 图片高度
                if (i == 0) {
                    width = buffer.getWidth();// 图片宽度
                }
                height += _height; // 获取总高度
                _imgRGB = new int[width * _height];// 从图片中读取RGB
                _imgRGB = buffer.getRGB(0, 0, width, _height, _imgRGB, 0, width);
                imgRGB.add(_imgRGB);
            }
            _height = 0; // 设置偏移高度为0
            // 生成新图片
            BufferedImage imageResult = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            for (int i = 0; i < picNum; i++) {
                __height = heightArray[i];
                if (i != 0)
                    _height += __height; // 计算偏移高度
                imageResult.setRGB(0, _height, width, __height, imgRGB.get(i), 0, width); // 写入流中
            }
            File outFile = new File(outPath);
            ImageIO.write(imageResult, "jpg", outFile);// 写图片
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	public static void main(String[] args) {
		new PdfUtils().pdf2OneImpage("E:\\test\\重大项目需求说明书.pdf");
	}
}
