package com.operation.SpringBootFileApi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.operation.SpringBootFileApi.model.FileEntity;
import com.operation.SpringBootFileApi.service.FileService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "File API")
@RestController
@RequestMapping("/FileResource")
public class FileResource {
	
	@Autowired
	FileService fileService;
	
	@ApiOperation(value="get an object by id")
	@GetMapping(value = "/getById/{id}")
	public FileEntity getFileById(@PathVariable Long id) {
		return fileService.getById(id);
	}
	@ApiOperation(value="create an file object")
	@PostMapping("/create")
	public FileEntity create(@RequestParam("file") MultipartFile file) {
		return fileService.create(file);
	}
	
	@ApiOperation(value="update an object")
	@PutMapping("/update/{id}")
	public FileEntity update(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
		return fileService.update(id,file);
	}
	
	@ApiOperation(value="delete an object")
	@DeleteMapping("/delete/{id}")
	public FileEntity delete(@PathVariable Long id) {
		return fileService.delete(id);
	}
	
	@ApiOperation(value="get all object list")
	@GetMapping(value = "/all")
    public List<FileEntity> getAll() {
		return fileService.getAll();
    }
	
	@ApiOperation(value="get file as byte array by file name")
	@GetMapping(value = "/getFile")
	public ResponseEntity<byte[]> getFileAsByteArray(@RequestParam("fileName") String fileName) {
		return fileService.getFileAsByteArray(fileName);
	}
	
	
}
