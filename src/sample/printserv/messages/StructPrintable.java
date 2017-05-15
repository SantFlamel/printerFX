package sample.printserv.messages;

import java.util.List;
import java.util.Map;

public class StructPrintable {
    public String Header;
    public String InfoOrg;
    public String InfoCheck;
    public List<String> Body;
//    public List<String> ItemOrders;
//    public String SumOrder;
//    public String Price;
//    public List<String> Discount;
//    public String ResultSumOrder;
//    public String PriceWithDiscount;
//    public String PriceCurrency;
//    public String TypeOperation;
//    public String TypePayment;
//    public String ShortChange;
    public String Thanks;
    public String Footer;
    public String OrgHash;
}

/*
type StructPrintable struct {
	Header            string
	InfoOrg           string
	InfoCheck         string
	ItemOrders        []string
	Price             string
	Discount          []string
	PriceWithDiscount string
	PriceCurrency     string
    TypePayment       string
	TypeOperation     string
	ShortChange       string
	Thanks            string
	Footer            string
    OrgHash           string
}


    ID              int64     //- идентификатор
	Order_id        int64     //- идентификатор заказа
	First_sure_name string    //- Фамилия Имя юзера
	UserHash        string    //- Хеш юзера
	RoleName        string    //- Имя роли
	OrgHash         string    //- хеш организации
	TypePayments    int64     //- тип оплаты
	TypeOperation   string    //- тип операции
	Cause           string    //- описание
	Deposit         float64   //- сумма внесения
	ShortChange     float64   //- сдача
	TimeOperation   time.Time //- время операции

*/