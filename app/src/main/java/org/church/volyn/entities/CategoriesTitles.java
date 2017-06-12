package org.church.volyn.entities;

import java.util.HashMap;

/**
 * Created by Admin on 03.11.2016.
 */

public class CategoriesTitles {
    public static final String BISHOPS_MINISTRY = "Служіння архієрея";
    public static final String DIOCESAN_NEWS = "Єпархіальні новини";
    public static final String DEANERIES_NEWS = "Новини з благочинь";
    public static final String SOCIAL_ACTIVITY = "Соціальне служіння";
    public static final String ACTIVITIES_OF_YOUTH_ORGANIZATIONS = "Діяльність молодіжних організацій";
    public static final String PUBLICATIONS = "Публікації та аналітика";
    public static final String CHILDREN_NEED_HELP = "Дітям потрібна допомога";
    public static final String ADVERTISEMENT = "Оголошення";
    public static final String TV_GRACE_FONT = "Благодатна купель";
    public static final String TV_VOLYN_BELLS = "Дзвони Волині";
    public static final String RADIO_SPIRITUAL_GUIDANCE = "Духовні орієнтири";
    public static final String RADIO_ORTHODOX_WORD = "Православне слово";

    public static HashMap<String, Integer> categoriesOrder = null;

    public static Integer getDirectoryOrder(String directoryName) {
        if (categoriesOrder == null) {
            init();
        }

        return categoriesOrder.get(directoryName);
    }

    private static void init() {

        categoriesOrder = new HashMap<String, Integer>();
        categoriesOrder.put(BISHOPS_MINISTRY, 1);
        categoriesOrder.put(DIOCESAN_NEWS, 2);
        categoriesOrder.put(SOCIAL_ACTIVITY, 3);
        categoriesOrder.put(ACTIVITIES_OF_YOUTH_ORGANIZATIONS, 4);
        categoriesOrder.put(DEANERIES_NEWS, 5);
        categoriesOrder.put(PUBLICATIONS, 6);
        categoriesOrder.put(CHILDREN_NEED_HELP, 7);
        categoriesOrder.put(ADVERTISEMENT, 8);
        categoriesOrder.put(TV_GRACE_FONT, 9);
        categoriesOrder.put(TV_VOLYN_BELLS, 10);
        categoriesOrder.put(RADIO_ORTHODOX_WORD, 11);
        categoriesOrder.put(RADIO_SPIRITUAL_GUIDANCE, 12);

    }

}
