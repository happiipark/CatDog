package com.kosta.catdog.service;

import java.io.File;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import com.kosta.catdog.entity.*;
import com.kosta.catdog.repository.ShopFileVORepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kosta.catdog.repository.ShopRepository;
import com.kosta.catdog.repository.UserDslRepository;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ShopServiceImpl implements ShopService {
	
	@Autowired
	private UserDslRepository userDslRepository;
	@Autowired
	private ShopRepository shopRepository;

	@Autowired
	private ShopFileVORepository shopFileVORepository;

	@Override
	public Shop addShop(Shop shop , List<MultipartFile> files) throws Exception {
		String dir = "/Users/baghaengbog/Desktop/Study/upload/shop";
		if(files!=null && files.size() !=0 ) {
			String fileNums = "";
			for (MultipartFile file : files) {

				Date today = Date.valueOf(LocalDate.now());


				ShopFileVO fileVO = new ShopFileVO();
				fileVO.setDir(dir);
				fileVO.setName(file.getOriginalFilename());
				fileVO.setSize(file.getSize());
				fileVO.setType(file.getContentType());
				fileVO.setDate(today);
				//fileVO.setData(file.getBytes());
				shopFileVORepository.save(fileVO);

				File uploadFile = new File(dir + fileVO.getNum());
				file.transferTo(uploadFile);
				if (!fileNums.equals(""))
					fileNums += ",";
				fileNums += fileVO.getNum();
			}
			shop.setProfImg(fileNums);
		}
		shopRepository.save(shop);
		return shop;
	}

//	@Override
//	public void addDesigner(String id, String position) throws Exception {
//		userDslRepository.addDesignerToShop(id, position);
//	}
	
	@Override
	public void deleteDesigner() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addShopInfo(Shop shop) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void addShopNotice(String notice) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Designer> designerListByShop(Integer num) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
