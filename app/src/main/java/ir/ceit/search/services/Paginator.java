package ir.ceit.search.services;

import java.util.ArrayList;
import java.util.List;

import ir.ceit.search.model.News;

public class Paginator {
    public int TOTAL_NUM_ITEMS;
    public int ITEMS_PER_PAGE = 10;
    public int ITEMS_REMAINING;
    public int LAST_PAGE;
    private List<News> news;

    public Paginator(int TOTAL_NUM_ITEMS, List<News> news) {
        this.TOTAL_NUM_ITEMS = TOTAL_NUM_ITEMS;
        ITEMS_REMAINING = TOTAL_NUM_ITEMS % ITEMS_PER_PAGE;
        LAST_PAGE = TOTAL_NUM_ITEMS / ITEMS_PER_PAGE;
        this.news = news;
    }

    public List<News> generatePage(int currentPage) {
        int startItem = currentPage * ITEMS_PER_PAGE;
        int numOfData = ITEMS_PER_PAGE;
        List<News> pageData = new ArrayList<>();

        if (news.size() <= ITEMS_PER_PAGE)
            return news;

        System.out.println("LOG Current page: " + currentPage + ", LAST PAGE: " + LAST_PAGE + ", ITEMS REMAINING: " + ITEMS_REMAINING + ", TOTAL NUM ITEMS: " + TOTAL_NUM_ITEMS + ", START ITEM: " + startItem);
        System.out.println("news size:" + news.size());
        if (currentPage == LAST_PAGE && ITEMS_REMAINING > 0) {
            for (int i = startItem; i < startItem + ITEMS_REMAINING; i++) {
                pageData.add(news.get(i));
            }
        } else {
            for (int i = startItem; i < startItem + numOfData; i++) {
                pageData.add(news.get(i));
            }
        }
        return pageData;
    }


}
