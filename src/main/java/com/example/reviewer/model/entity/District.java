package com.example.reviewer.model.entity;

public enum District {
    BARANOVICHI("Барановичский", Region.BREST), BEREZA("Берёзовский", Region.BREST),
    BREST("Брестский", Region.BREST), GANCEVICHI("Ганцевичский", Region.BREST),
    DROGICHIN("Дрогичинский", Region.BREST), ZHABINKA("Жабинковский", Region.BREST),
    IVANOVO("Ивановский", Region.BREST), IVACEVICHI("Ивацевичский", Region.BREST),
    KAMENETS("Каменецкий", Region.BREST), KOBRIN("Кобринский", Region.BREST),
    LUNINETS("Лунинецкий", Region.BREST), LYAKHOVICHI("Ляховичский", Region.BREST),
    MALORITA("Малоритский", Region.BREST), PINSK("Пинский", Region.BREST),
    PRUZHANU("Пружанский", Region.BREST), STOLIN("Столинский", Region.BREST),

    BESHENKOVICHI("Бешенковичский", Region.VITEBSK), BRASLAV("Браславский", Region.VITEBSK),
    VERHNEDVINSK("Верхнедвинский", Region.VITEBSK), VITEBSK("Витебский", Region.VITEBSK),
    GLUBOKOE("Глубокский", Region.VITEBSK), GORODOK("Городокский", Region.VITEBSK),
    DOKSHITSU("Докшицкий", Region.VITEBSK), DUBROVNO("Дубровенский", Region.VITEBSK),
    LEPEL("Лепельский", Region.VITEBSK), LIOZNO("Лиозненский", Region.VITEBSK),
    MIORU("Миорский", Region.VITEBSK), ORSHA("Оршанский", Region.VITEBSK),
    POLOTSK("Полоцкий", Region.VITEBSK), POSTAVU("Поставский", Region.VITEBSK),
    ROSSONU("Россонский", Region.VITEBSK), SENNO("Сенненский", Region.VITEBSK),
    TOLOCHIN("Толочинский", Region.VITEBSK), USHACHI("Ушачский", Region.VITEBSK),
    CHASHNIKI("Чашникский", Region.VITEBSK), SHARKOVSHCHINA("Шарковщинский", Region.VITEBSK),
    SHUMILINO("Шумилинский", Region.VITEBSK),

    BRAGIN("Брагинский", Region.GOMEL), BUDAKOSHELEVO("Буда-Кошелевский", Region.GOMEL),
    VETKA("Ветковский", Region.GOMEL), GOMEL("Гомельский", Region.GOMEL),
    DOBRUSH("Добрушский", Region.GOMEL), ELSK("Ельский", Region.GOMEL),
    ZHITKOVICHI("Житковичский", Region.GOMEL), ZHLOBIN("Жлобинский", Region.GOMEL),
    KALINKOVICHI("Калинковичский", Region.GOMEL), KORMA("Кормянский", Region.GOMEL),
    LELCHITSU("Лельчицкий", Region.GOMEL), LOEV("Лоевский", Region.GOMEL),
    MOZUR("Мозырский", Region.GOMEL), NAROVLYA("Наровлянский", Region.GOMEL),
    OKTYABRSKIY("Октябрьский", Region.GOMEL), PETRIKOV("Петриковский", Region.GOMEL),
    RECHITSA("Речицкий", Region.GOMEL), ROGACHEV("Рогачевский", Region.GOMEL),
    SVETLOGORSK("Светлогорский", Region.GOMEL), HOYNIKI("Хойникский", Region.GOMEL),
    CHECHERSK("Чечерский", Region.GOMEL),

    BERESTOVICA("Берестовицкий", Region.GRODNO), VOLKOVUSK("Волковысский", Region.GRODNO),
    VORONOVO("Вороновский", Region.GRODNO), GRODNO("Гродненский", Region.GRODNO),
    DYATLOVO("Дятловский", Region.GRODNO), ZELVA("Зельвенский", Region.GRODNO),
    IVYE("Ивьевский", Region.GRODNO), KORELICHI("Кореличский", Region.GRODNO),
    LIDA("Лидский", Region.GRODNO), MOSTU("Мостовский", Region.GRODNO),
    NOVOGRUDOK("Новогрудский", Region.GRODNO), OSTROVETS("Островецкий", Region.GRODNO),
    OSHMYANU("Ошмянский", Region.GRODNO), SVISLOCH("Свислочский", Region.GRODNO),
    SLONIM("Слонимский", Region.GRODNO), SMORGON("Сморгонский", Region.GRODNO),
    SHCHUCHIN("Щучинский", Region.GRODNO),

    BEREZINO("Березинский", Region.MINSK), BORISOV("Борисовский", Region.MINSK),
    VILEYKA("Вилейский", Region.MINSK), VOLOZHIN("Воложинский", Region.MINSK),
    DZERZHINSK("Дзержинский", Region.MINSK), KLETSK("Клецкий", Region.MINSK),
    KOPUL("Копыльский", Region.MINSK), KRUPKI("Крупский", Region.MINSK),
    LOGOYSK("Логойский", Region.MINSK), LYUBAN("Любанский", Region.MINSK),
    MINSK("Минский", Region.MINSK), MOLODECHNO("Молодечненский", Region.MINSK),
    MYADEL("Мядельский", Region.MINSK), NESVIZH("Несвижский", Region.MINSK),
    PUHOVICHI("Пуховичский", Region.MINSK), SLUTSK("Слуцкий", Region.MINSK),
    SMOLEVICHI("Смолевичский", Region.MINSK), SOLIGORSK("Солигорский", Region.MINSK),
    STARAYADOROGA("Стародорожский", Region.MINSK), STOLBCU("Столбцовский", Region.MINSK),
    UZDA("Узденский", Region.MINSK), CHERVEN("Червенский", Region.MINSK),

    BELUNICHI("Белыничский", Region.MOGILEV), BOBRUYSK("Бобруйский", Region.MOGILEV),
    BUHOV("Быховский", Region.MOGILEV), GLUSK("Глусский", Region.MOGILEV),
    GORKI("Горецкий", Region.MOGILEV), DRIBIN("Дрибинский", Region.MOGILEV),
    KIROVSK("Кировский", Region.MOGILEV), KLIMOVICHI("Климовичский", Region.MOGILEV),
    KLICHEV("Кличевский", Region.MOGILEV), KOSTYUKOVICHI("Костюковичский", Region.MOGILEV),
    KRASNOPOLYE("Краснопольский", Region.MOGILEV), KRICHEV("Кричевский", Region.MOGILEV),
    KRUGLOE("Круглянский", Region.MOGILEV), MOGILEV("Могилевский", Region.MOGILEV),
    MSTISLAVL("Мстиславский", Region.MOGILEV), OSIPOVICHI("Осиповичский", Region.MOGILEV),
    SLAVGOROD("Славгородский", Region.MOGILEV), HOTIMSK("Хотимский", Region.MOGILEV),
    CHAUSU("Чаусский", Region.MOGILEV), CHERIKOV("Чериковский", Region.MOGILEV),
    SHKLOV("Шкловский", Region.MOGILEV),;

    private final String name;
    private final Region region;

    District(String name, Region region) {
        this.name = name;
        this.region = region;
    }

    public String getName() {
        return name;
    }

    public Region getRegion() {
        return region;
    }

    @Override
    public String toString() {
        return name + " район, " + region;
    }
}
