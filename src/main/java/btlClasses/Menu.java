package btlClasses;

public enum Menu {

    EXERCISE("מיצוי זכויות", "ctl00_Topmneu_BenefitsHyperLink"),
    BENEFITS("קצבאות והטבות", "ctl00_Topmneu_HyperLink3"),
    INSURANCE("דמי ביטוח", "ctl00_Topmneu_InsuranceHyperLink"),
    CONTACT("יצירת קשר", "ctl00_Topmneu_ContactsHyperLink");

    private final String menuText;
    private final String id;

    Menu(String menuText, String id) {
        this.menuText = menuText;
        this.id = id;
    }

    public String getMenuText() {
        return menuText;
    }

    public String getId() {
        return id;
    }
}