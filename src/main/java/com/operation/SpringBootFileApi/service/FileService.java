package com.operation.SpringBootFileApi.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.operation.SpringBootFileApi.exception.DocumentStorageException;
import com.operation.SpringBootFileApi.model.FileEntity;
import com.operation.SpringBootFileApi.repo.FileRepository;
import com.operation.SpringBootFileApi.utils.FileStorageProperties;
import com.operation.SpringBootFileApi.utils.MessageConstant;

@Service
public class FileService {

	@Autowired
	FileRepository fileRepository;

	private final Path fileStorageLocation;

	/*
	 * png, jpeg, jpg, docx, pdf, xlsx 
	 */
	private static final  List<String> EXTENSIONS= Arrays.asList("png","jpeg","jpg","docx","pdf","xlsx");
	
	
	@Autowired
	public FileService(FileStorageProperties file) {
		this.fileStorageLocation = Paths.get(file.getUploadDir()).toAbsolutePath().normalize();
		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (Exception ex) {
			throw new DocumentStorageException(
					"Could not create the directory where the uploaded files will be stored.", ex);
		}
	}
	
	
	private boolean validateExtensions(String extension) throws Exception{
		if(EXTENSIONS.stream().anyMatch(x->x.equalsIgnoreCase(extension))) {
			return true;
		}else {
			return false;
		}
	}

	private FileEntity saveFile(MultipartFile file) {
		FileEntity response = new FileEntity();
		try {

			String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
			String fileName = "";

			if (originalFileName.contains("..")) {
				response.getMessage().setCode(MessageConstant.getFailed());
				response.getMessage().setMessage("File name can not has contains '..' ");
				return response;
			}

			String fileExtension = "";
			try {
				fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
				
				if(validateExtensions(fileExtension.substring(1,fileExtension.length()))) {
					response.getMessage().setCode(MessageConstant.getFailed());
					response.getMessage().setMessage("File extension is not correct");
					return response;
				}
				
			} catch (Exception e) {
				response.getMessage().setCode(MessageConstant.getFailed());
				response.getMessage().setMessage("File extension can't be empty");
				return response;
			}
			
			if(file.getSize()>5242880L) {
				response.getMessage().setCode(MessageConstant.getFailed());
				response.getMessage().setMessage("File is so huge");
				return response;
			}
			
			fileName = validateFileName(originalFileName).toLowerCase();

			Path targetLocation = this.fileStorageLocation.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

			response.getMessage().setCode(MessageConstant.getSuccess());
			response.setContentType(file.getContentType());
			response.setExtension(fileExtension);
			response.setName(fileName);
			response.setPath(targetLocation.toString());
			response.setSize(file.getSize());

		} catch (Exception ex) {
			ex.printStackTrace();
			response.getMessage().setCode(MessageConstant.getFailed());
			response.getMessage().setMessage("Could not store file. Please try again!" + ex.getMessage());
		}
		return response;
	}

	private FileEntity deleteFile(String fileName) {
		FileEntity response = new FileEntity();
		try {
			File path = new File(loadFileAsResource(fileName).getURI());
			if (path.delete()) {
				response.getMessage().setCode(MessageConstant.getSuccess());
			} else {
				response.getMessage().setCode(MessageConstant.getFailed());
				response.getMessage().setMessage("File could not delete. Please try again");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			response.getMessage().setCode(MessageConstant.getFailed());
			response.getMessage().setMessage("Catched an error when deleting the file ! " + ex.getMessage());
		}
		return response;
	}

	public FileEntity create(MultipartFile file) {

		try {
			FileEntity savedFile = saveFile(file);

			if (savedFile.getMessage().isFailed()) {
				FileEntity response = new FileEntity();
				response.getMessage().setCode(savedFile.getMessage().getCode());
				response.getMessage().setMessage(savedFile.getMessage().getMessage());
				return response;
			}

			fileRepository.save(savedFile);
			return savedFile;
		} catch (Exception ex) {
			FileEntity response = new FileEntity();
			response.getMessage().setCode(MessageConstant.getFailed());
			response.getMessage().setMessage("Catched an error when creating the file ! " + ex.getMessage());
			return response;
		}
	}

	public FileEntity update(Long id, MultipartFile file) {

		try {

			if (id == null) {
				FileEntity response = new FileEntity();
				response.getMessage().setCode(MessageConstant.getFailed());
				response.getMessage().setMessage("Id can not be null");
				return response;
			}

			Optional<FileEntity> optional = fileRepository.findById(id);
			if (!optional.isPresent() || optional.get().getId() == null) {
				FileEntity response = new FileEntity();
				response.getMessage().setCode(MessageConstant.getFailed());
				response.getMessage().setMessage("File is not found");
				return response;
			}
			FileEntity entityDb = optional.get();

			FileEntity savedFile = saveFile(file);

			if (savedFile.getMessage().isFailed()) {
				FileEntity response = new FileEntity();
				response.getMessage().setCode(savedFile.getMessage().getCode());
				response.getMessage().setMessage(savedFile.getMessage().getMessage());
				return response;
			}

			FileEntity deletedFile = deleteFile(entityDb.getName());
			if (deletedFile.getMessage().isFailed()) {
				FileEntity response = new FileEntity();
				response.getMessage().setCode(deletedFile.getMessage().getCode());
				response.setMessage(deletedFile.getMessage());
				return response;
			}

			entityDb.setContentType(savedFile.getContentType());
			entityDb.setExtension(savedFile.getExtension());
			entityDb.setName(savedFile.getName());
			entityDb.setPath(savedFile.getPath());
			entityDb.setSize(savedFile.getSize());
		   fileRepository.save(entityDb);
		   
		   entityDb.getMessage().setCode(MessageConstant.getSuccess());
		   
		   return entityDb;
		} catch (Exception ex) {
			FileEntity response = new FileEntity();
			response.getMessage().setCode(MessageConstant.getFailed());
			response.getMessage().setMessage("Catched an error when updating the file ! " + ex.getMessage());
			return response;
		}
	}

	public FileEntity getById(Long id) {

		try {

			if (id == null) {
				FileEntity response = new FileEntity();
				response.getMessage().setCode(MessageConstant.getFailed());
				response.getMessage().setMessage("Id can not be null");
				return response;
			}

			Optional<FileEntity> optional = fileRepository.findById(id);
			if (!optional.isPresent() || optional.get().getId() == null) {
				FileEntity response = new FileEntity();
				response.getMessage().setCode(MessageConstant.getFailed());
				response.getMessage().setMessage("File is not found");
				return response;
			}
			FileEntity file= optional.get();
			file.getMessage().setCode(MessageConstant.getSuccess());
			return file;
		} catch (Exception ex) {
			FileEntity response = new FileEntity();
			response.getMessage().setCode(MessageConstant.getFailed());
			response.getMessage().setMessage("Could not store file .Please try again!" + ex.getMessage());
			return response;
		}
	}

	public FileEntity delete(Long id) {
		FileEntity response = new FileEntity();
		try {

			if (id == null) {
				response.getMessage().setCode(MessageConstant.getFailed());
				response.getMessage().setMessage("Id can not be null");
				return response;
			}

			Optional<FileEntity> optional = fileRepository.findById(id);
			
			if (!optional.isPresent() || optional.get().getId() == null) {
				response.getMessage().setCode(MessageConstant.getFailed());
				response.getMessage().setMessage("File is not found");
				return response;
			}
			
			FileEntity entityDb = optional.get();

			FileEntity entity = deleteFile(entityDb.getName());
			if (entity.getMessage().isFailed()) {
				response.getMessage().setCode(entity.getMessage().getCode());
				response.setMessage(entity.getMessage());
				return response;
			}

			fileRepository.delete(entityDb);

			response.getMessage().setCode(MessageConstant.getSuccess());
			response.getMessage().setMessage("File is deleted..");

		} catch (Exception ex) {
			response.getMessage().setCode(MessageConstant.getFailed());
			response.getMessage().setMessage("Catched an error when deleting the file ! " + ex.getMessage());

		}
		return response;
	}

	public java.util.List<FileEntity> getAll() {
		// Normalize file name
		try {
			return fileRepository.findAll();
		} catch (Exception ex) {
			System.out.println(ex);
		}
		return null;
	}

	public Resource loadFileAsResource(String fileName) throws Exception {
		try {
			Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				throw new FileNotFoundException("File not found " + fileName);
			}
		} catch (MalformedURLException ex) {
			throw new FileNotFoundException("File not found " + fileName);
		}
	}

	public byte[] loadFileAsByteArray(String fileName) throws Exception {
		try {
			File path = new File(loadFileAsResource(fileName).getURI());

			FileInputStream fl = new FileInputStream(path);
			byte[] arr = new byte[(int) path.length()];

			fl.read(arr);

			fl.close();

			return arr;
		} catch (Exception ex) {
			throw new FileNotFoundException("File not found " + fileName + " " + ex.getMessage());
		}
	}

	public ResponseEntity<byte[]> getFileAsByteArray(String fileName) {
		try {

			if (!StringUtils.hasLength(fileName)) {
				return ResponseEntity.notFound().build();
			}

			FileEntity fileEntity = fileRepository.findByName(fileName);
			if (fileEntity == null) {
				return ResponseEntity.notFound().build();
			}

			byte[] bytes = loadFileAsByteArray(fileName);
			if (bytes == null) {
				return ResponseEntity.notFound().build();
			}

			MediaType mediaTip = MediaType.parseMediaType(fileEntity.getContentType());
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Disposition", "attachment; filename=\"" + fileEntity.getName() + "\"");
			headers.add("Cache-Control", "no-cache");
			return ResponseEntity.ok().headers(headers).contentLength(fileEntity.getSize()).contentType(mediaTip)
					.body(bytes);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.notFound().build();
		}

	}

	public static String validateFileName(String name) throws Exception {
		name = name.replace(" ", "_");
		name = name.replace("ğ", "g");
		name = name.replace("ü", "u");
		name = name.replace("ş", "s");
		name = name.replace("ı", "i");
		name = name.replace("ö", "o");
		name = name.replace("ç", "c");
		name = name.replace("ü", "U");
		name = name.replace("Ğ", "G");
		name = name.replace("Ş", "S");
		name = name.replace("İ", "I");
		name = name.replace("Ö", "O");
		name = name.replace("Ç", "C");
		name = name.replace("/", "_");
		name = name.replace("\\", "_");
		name = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "_" + name;
		if (name.length() > 250) {
			name = name.substring(0, 248);
		}
		return name;
	}

}
