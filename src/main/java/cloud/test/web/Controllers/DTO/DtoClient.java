package cloud.test.web.Controllers.DTO;

public class DtoClient {
    private String name;
    private String phone;
    private String address;
    private boolean verified;
    private float bill;

    public DtoClient() {
    }

    public DtoClient(String name, String phone, String address) {
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    public DtoClient(String name, String phone, String address, boolean verified, float bill) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.verified = verified;
        this.bill = bill;
    }

    public DtoClient(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getPhone() { return phone; }

    public String getAddress() {
        return address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isVerified() { return verified; }

    public void setVerified(boolean verified) { this.verified = verified; }

    public float getBill() { return bill; }

    public void setBill(float bill) { this.bill = bill; }
}
