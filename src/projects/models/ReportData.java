package projects.models;

// Model cho việc đóng gói các kết quả thống kê
public class ReportData {
    private double totalRevenue;
    private int totalTicketsSold;
    private int totalCustomers;
    private int totalActiveFlights;
    private String reportPeriod; //date

    // Constructor mặc định
    public ReportData() { 
        this(0, 0, 0, 0, "Hôm nay");
    }
    
    // Constructor đầy đủ tham số
    public ReportData(double totalRevenue, int totalTicketsSold, int totalCustomers, int totalActiveFlights, String reportPeriod) {
        this.totalRevenue = totalRevenue;
        this.totalTicketsSold = totalTicketsSold;
        this.totalCustomers = totalCustomers;
        this.totalActiveFlights = totalActiveFlights;
        this.reportPeriod = reportPeriod;
    }

    // =======================================================
    // --- GETTERS và SETTERS  ---
    // =======================================================
    
    public double getTotalRevenue() { 
        return totalRevenue; 
    }
    
    public void setTotalRevenue(double totalRevenue) { 
        this.totalRevenue = totalRevenue; 
    }

    public int getTotalTicketsSold() {
        return totalTicketsSold;
    }

    public void setTotalTicketsSold(int totalTicketsSold) {
        this.totalTicketsSold = totalTicketsSold;
    }

    public int getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(int totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public int getTotalActiveFlights() {
        return totalActiveFlights;
    }

    public void setTotalActiveFlights(int totalActiveFlights) {
        this.totalActiveFlights = totalActiveFlights;
    }

    public String getReportPeriod() {
        return reportPeriod;
    }

    public void setReportPeriod(String reportPeriod) {
        this.reportPeriod = reportPeriod;
    }
}