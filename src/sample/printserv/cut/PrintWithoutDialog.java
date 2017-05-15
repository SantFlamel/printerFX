package sample.printserv.cut;

import javax.print.PrintService;
import java.awt.*;
import java.awt.print.*;

public class PrintWithoutDialog implements Printable
{
    @Override
    public int print(Graphics g, PageFormat pf, int page) throws PrinterException
    {
        if (page > 0) { /* У нас только одна страница, а отсчёт начинается с нуля*/
            return NO_SUCH_PAGE;
        }

        Font f = new Font("Roman", 0, 10);
        g.setFont(f);
        g.drawString("", 10, 100);




        return PAGE_EXISTS;
    }

    public PrintWithoutDialog(PrintService ps)
    {
        PrinterJob job = PrinterJob.getPrinterJob();
        if(ps!=null){
            try {
                job.setPrintService(ps);
            } catch (PrinterException e) {
                e.printStackTrace();
            }
            // Выставляем printable
            job.setPrintable(this);
            // Инициируем печать
            try {
                job.print();
            } catch (PrinterException e) {
                e.printStackTrace();
            }
        }else {
            System.out.println("Принтер не найден");
        }
    }
}