package cloud.test.web.Controllers.DTO;


public class DtoBucketNotification {

    private String kind;
    private String id;
    private String selfLink;
    private String name;
    private String bucket;
    private long generation;
    private int metageneration;
    private String contentType;
    private String updated;
    private int size;
    private String md5hash;
    private String mediaLink;
    private String crc32c;
    private String etag;

    public DtoBucketNotification() {
    }

    public DtoBucketNotification(String kind, String id, String selfLink, String name, String bucket, long generation, int metageneration, String contentType, String updated, int size, String md5hash, String mediaLink, String crc32c, String etag) {
        this.kind = kind;
        this.id = id;
        this.selfLink = selfLink;
        this.name = name;
        this.bucket = bucket;
        this.generation = generation;
        this.metageneration = metageneration;
        this.contentType = contentType;
        this.updated = updated;
        this.size = size;
        this.md5hash = md5hash;
        this.mediaLink = mediaLink;
        this.crc32c = crc32c;
        this.etag = etag;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public long getGeneration() {
        return generation;
    }

    public void setGeneration(long generation) {
        this.generation = generation;
    }

    public int getMetageneration() {
        return metageneration;
    }

    public void setMetageneration(int metageneration) {
        this.metageneration = metageneration;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getMd5hash() {
        return md5hash;
    }

    public void setMd5hash(String md5hash) {
        this.md5hash = md5hash;
    }

    public String getMediaLink() {
        return mediaLink;
    }

    public void setMediaLink(String mediaLink) {
        this.mediaLink = mediaLink;
    }

    public String getCrc32c() {
        return crc32c;
    }

    public void setCrc32c(String crc32c) {
        this.crc32c = crc32c;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }
}
