package io.github.mrergos.dto;

import java.io.InputStream;

public record FileDownloadResponse (InputStream inputStream, String filename, String contentType) {

}
