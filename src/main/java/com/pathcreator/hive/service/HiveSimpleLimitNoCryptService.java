package com.pathcreator.hive.service;

import jakarta.ws.rs.core.Response;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public interface HiveSimpleLimitNoCryptService {

    Response save(String uniq, InputStream inputStream);
}