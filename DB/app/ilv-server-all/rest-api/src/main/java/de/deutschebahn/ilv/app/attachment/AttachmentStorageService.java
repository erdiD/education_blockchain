package de.deutschebahn.ilv.app.attachment;

import de.deutschebahn.ilv.app.ClientException;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

/**
 * Created by AlbertLacambraBasil on 01.08.2017.
 */
public class AttachmentStorageService {

    private static final String BO_DOCUMENT_STORE_KEY = "ilv_store_key";
    private static final String SEPARATOR = FileSystems.getDefault().getSeparator();
    private static final String HASH_FUNCTION = "SHA-256";

    @Inject
    Logger logger;

    public String storeAttachment(Attachment attachment) {
        try {

            MessageDigest digest = MessageDigest.getInstance(HASH_FUNCTION);
            InputStream data = new DigestInputStream(attachment.getFileData(), digest);
            Path location = getStoragePath();
            Path p = location.resolve(Paths.get(attachment.getFileId()));
            Files.copy(data, p);
            byte[] digestBytes = digest.digest();
            attachment.setFileHash(digestBytes);

            logger.info("[storeAttachment] Saved file to " + p);
            logger.info("[storeAttachment] Saved file with digest " + attachment.getFileHash());

        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }

        return attachment.getFileId();
    }

    public ByteArrayOutputStream getAttachment(Attachment attachment) {
        try {
            Path location = getStoragePath();
            Path p = location.resolve(Paths.get(attachment.getFileId()));

            if (!Files.exists(p)) {
                throw ClientException.createNotFoundError(attachment.getFileId(), "File");
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Files.copy(p, outputStream);
            return outputStream;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path getStoragePath() throws IOException {
        String location = System.getenv(BO_DOCUMENT_STORE_KEY);

        if (location == null || location.isEmpty()) {
            location = System.getProperty("user.home") + SEPARATOR + "ilv-document-storage";
        }

        logger.info("[getStoragePath] using location=" + location);

        Path storagePath = Paths.get(location);
        if (!Files.exists(storagePath)) {
            storagePath = Files.createDirectory(storagePath);
        }

        return storagePath;
    }
}
