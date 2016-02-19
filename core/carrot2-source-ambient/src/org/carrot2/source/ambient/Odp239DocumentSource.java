
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source.ambient;

import org.carrot2.core.ProcessingException;
import org.carrot2.core.attribute.*;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.IntRange;

/**
 * Serves documents from the ODP239 test set. For more details, please see:
 * http://credo.fub.it/odp239/.
 */
@Bindable(prefix = "Odp239DocumentSource", inherit = CommonAttributes.class)
public class Odp239DocumentSource extends FubDocumentSource
{
    static final FubTestCollection DATA = new FubTestCollection("/odp239");

    static final int TOPIC_COUNT = 239;
    static final int MAX_RESULTS_PER_TOPIC = 1000;

    /**
     * ODP239 Topic. The ODP239 Topic to load documents from.
     */
    @Input
    @Processing
    @Attribute
    @Required
    @Group(TOPIC_ID)
    @Level(AttributeLevel.BASIC)
    public Odp239Topic topic = Odp239Topic.ARTS_ANIMATION;

    @Processing
    @Input
    @Attribute(key = AttributeNames.RESULTS, inherit = true)
    @IntRange(min = 1, max = MAX_RESULTS_PER_TOPIC)
    public int results = MAX_RESULTS_PER_TOPIC;

    @Processing
    @Output
    @Attribute(key = AttributeNames.RESULTS_TOTAL, inherit = true)
    public long resultsTotal = MAX_RESULTS_PER_TOPIC;

    /**
     * All available ODP239 topics.
     */
    public static enum Odp239Topic
    {
        ARTS_ANIMATION(1, "Arts > Animation"),
        ARTS_ARCHITECTURE(2, "Arts > Architecture"),
        ARTS_BODYART(3, "Arts > Bodyart"),
        ARTS_COMICS(4, "Arts > Comics"),
        ARTS_CRAFTS(5, "Arts > Crafts"),
        ARTS_EDUCATION(6, "Arts > Education"),
        ARTS_ILLUSTRATION(7, "Arts > Illustration"),
        ARTS_LITERATURE(8, "Arts > Literature"),
        ARTS_MOVIES(9, "Arts > Movies"),
        ARTS_MUSIC(10, "Arts > Music"),
        ARTS_ONLINE_WRITING(11, "Arts > Online Writing"),
        ARTS_PEOPLE(12, "Arts > People"),
        ARTS_PERFORMING_ARTS(13, "Arts > Performing Arts"),
        ARTS_PHOTOGRAPHY(14, "Arts > Photography"),
        ARTS_RADIO(15, "Arts > Radio"),
        ARTS_TELEVISION(16, "Arts > Television"),
        ARTS_VIDEO(17, "Arts > Video"),
        ARTS_VISUAL_ARTS(18, "Arts > Visual Arts"),
        ARTS_WRITERS_RESOURCES(19, "Arts > Writers Resources"),
        BUSINESS_AGRICULTURE_AND_FORESTRY(20, "Business > Agriculture and Forestry"),
        BUSINESS_ARTS_AND_ENTERTAINMENT(21, "Business > Arts and Entertainment"),
        BUSINESS_AUTOMOTIVE(22, "Business > Automotive"),
        BUSINESS_BUSINESS_SERVICES(23, "Business > Business Services"),
        BUSINESS_CHEMICALS(24, "Business > Chemicals"),
        BUSINESS_CONSTRUCTION_AND_MAINTENANCE(25, "Business > Construction and Maintenance"),
        BUSINESS_CONSUMER_GOODS_AND_SERVICES(26, "Business > Consumer Goods and Services"),
        BUSINESS_ECOMMERCE(27, "Business > E-Commerce"),
        BUSINESS_EDUCATION_AND_TRAINING(28, "Business > Education and Training"),
        BUSINESS_ELECTRONICS_AND_ELECTRICAL(29, "Business > Electronics and Electrical"),
        BUSINESS_ENERGY(30, "Business > Energy"),
        BUSINESS_FINANCIAL_SERVICES(31, "Business > Financial Services"),
        BUSINESS_FOOD_AND_RELATED_PRODUCTS(32, "Business > Food and Related Products"),
        BUSINESS_HEALTHCARE(33, "Business > Healthcare"),
        BUSINESS_HOSPITALITY(34, "Business > Hospitality"),
        BUSINESS_HUMAN_RESOURCES(35, "Business > Human Resources"),
        BUSINESS_INDUSTRIAL_GOODS_AND_SERVICES(36, "Business > Industrial Goods and Services"),
        BUSINESS_INFORMATION_TECHNOLOGY(37, "Business > Information Technology"),
        BUSINESS_INVESTING(38, "Business > Investing"),
        BUSINESS_MANAGEMENT(39, "Business > Management"),
        BUSINESS_MARKETING_AND_ADVERTISING(40, "Business > Marketing and Advertising"),
        BUSINESS_MATERIALS(41, "Business > Materials"),
        BUSINESS_OPPORTUNITIES(42, "Business > Opportunities"),
        BUSINESS_REAL_ESTATE(43, "Business > Real Estate"),
        BUSINESS_RETAIL_TRADE(44, "Business > Retail Trade"),
        BUSINESS_SMALL_BUSINESS(45, "Business > Small Business"),
        BUSINESS_TELECOMMUNICATIONS(46, "Business > Telecommunications"),
        BUSINESS_TEXTILES_AND_NONWOVENS(47, "Business > Textiles and Nonwovens"),
        BUSINESS_TRANSPORTATION_AND_LOGISTICS(48, "Business > Transportation and Logistics"),
        COMPUTERS_ALGORITHMS(49, "Computers > Algorithms"),
        COMPUTERS_ARTIFICIAL_INTELLIGENCE(50, "Computers > Artificial Intelligence"),
        COMPUTERS_ARTIFICIAL_LIFE(51, "Computers > Artificial Life"),
        COMPUTERS_CAD_AND_CAM(52, "Computers > CAD and CAM"),
        COMPUTERS_COMPANIES(53, "Computers > Companies"),
        COMPUTERS_COMPUTER_SCIENCE(54, "Computers > Computer Science"),
        COMPUTERS_CONSULTANTS(55, "Computers > Consultants"),
        COMPUTERS_DATA_COMMUNICATIONS(56, "Computers > Data Communications"),
        COMPUTERS_DATA_FORMATS(57, "Computers > Data Formats"),
        COMPUTERS_EMULATORS(58, "Computers > Emulators"),
        COMPUTERS_GRAPHICS(59, "Computers > Graphics"),
        COMPUTERS_HACKING(60, "Computers > Hacking"),
        COMPUTERS_HARDWARE(61, "Computers > Hardware"),
        COMPUTERS_INTERNET(62, "Computers > Internet"),
        COMPUTERS_MOBILE_COMPUTING(63, "Computers > Mobile Computing"),
        COMPUTERS_MULTIMEDIA(64, "Computers > Multimedia"),
        COMPUTERS_OPEN_SOURCE(65, "Computers > Open Source"),
        COMPUTERS_PARALLEL_COMPUTING(66, "Computers > Parallel Computing"),
        COMPUTERS_PROGRAMMING(67, "Computers > Programming"),
        COMPUTERS_ROBOTICS(68, "Computers > Robotics"),
        COMPUTERS_SECURITY(69, "Computers > Security"),
        COMPUTERS_SOFTWARE(70, "Computers > Software"),
        COMPUTERS_SPEECH_TECHNOLOGY(71, "Computers > Speech Technology"),
        COMPUTERS_SYSTEMS(72, "Computers > Systems"),
        COMPUTERS_USENET(73, "Computers > Usenet"),
        COMPUTERS_VIRTUAL_REALITY(74, "Computers > Virtual Reality"),
        GAMES_BOARD_GAMES(75, "Games > Board Games"),
        GAMES_GAMBLING(76, "Games > Gambling"),
        GAMES_MINIATURES(77, "Games > Miniatures"),
        GAMES_ROLEPLAYING(78, "Games > Roleplaying"),
        GAMES_TRADING_CARD_GAMES(79, "Games > Trading Card Games"),
        GAMES_VIDEO_GAMES(80, "Games > Video Games"),
        HEALTH_ALTERNATIVE(81, "Health > Alternative"),
        HEALTH_ANIMAL(82, "Health > Animal"),
        HEALTH_BEAUTY(83, "Health > Beauty"),
        HEALTH_CHILD_HEALTH(84, "Health > Child Health"),
        HEALTH_CONDITIONS_AND_DISEASES(85, "Health > Conditions and Diseases"),
        HEALTH_DENTISTRY(86, "Health > Dentistry"),
        HEALTH_FITNESS(87, "Health > Fitness"),
        HEALTH_MEDICINE(88, "Health > Medicine"),
        HEALTH_MENTAL_HEALTH(89, "Health > Mental Health"),
        HEALTH_NURSING(90, "Health > Nursing"),
        HEALTH_NUTRITION(91, "Health > Nutrition"),
        HEALTH_OCCUPATIONAL_HEALTH_AND_SAFETY(92, "Health > Occupational Health and Safety"),
        HEALTH_PROFESSIONS(93, "Health > Professions"),
        HEALTH_PUBLIC_HEALTH_AND_SAFETY(94, "Health > Public Health and Safety"),
        HEALTH_REPRODUCTIVE_HEALTH(95, "Health > Reproductive Health"),
        HEALTH_SENIOR_HEALTH(96, "Health > Senior Health"),
        HEALTH_WOMENS_HEALTH(97, "Health > Women's Health"),
        HOME_CONSUMER_INFORMATION(98, "Home > Consumer Information"),
        HOME_COOKING(99, "Home > Cooking"),
        HOME_FAMILY(100, "Home > Family"),
        HOME_GARDENING(101, "Home > Gardening"),
        HOME_HOME_IMPROVEMENT(102, "Home > Home Improvement"),
        HOME_PERSONAL_FINANCE(103, "Home > Personal Finance"),
        KIDS_AND_TEENS_ARTS(104, "Kids and Teens > Arts"),
        KIDS_AND_TEENS_ENTERTAINMENT(105, "Kids and Teens > Entertainment"),
        KIDS_AND_TEENS_GAMES(106, "Kids and Teens > Games"),
        KIDS_AND_TEENS_HEALTH(107, "Kids and Teens > Health"),
        KIDS_AND_TEENS_INTERNATIONAL(108, "Kids and Teens > International"),
        KIDS_AND_TEENS_PEOPLE_AND_SOCIETY(109, "Kids and Teens > People and Society"),
        KIDS_AND_TEENS_PRESCHOOL(110, "Kids and Teens > Pre-School"),
        KIDS_AND_TEENS_SCHOOL_TIME(111, "Kids and Teens > School Time"),
        KIDS_AND_TEENS_SPORTS_AND_HOBBIES(112, "Kids and Teens > Sports and Hobbies"),
        KIDS_AND_TEENS_TEEN_LIFE(113, "Kids and Teens > Teen Life"),
        NEWS_MEDIA(114, "News > Media"),
        NEWS_NEWSPAPERS(115, "News > Newspapers"),
        NEWS_WEATHER(116, "News > Weather"),
        RECREATION_ANTIQUES(117, "Recreation > Antiques"),
        RECREATION_AUDIO(118, "Recreation > Audio"),
        RECREATION_AUTOS(119, "Recreation > Autos"),
        RECREATION_AVIATION(120, "Recreation > Aviation"),
        RECREATION_BIRDING(121, "Recreation > Birding"),
        RECREATION_BOATING(122, "Recreation > Boating"),
        RECREATION_CAMPS(123, "Recreation > Camps"),
        RECREATION_CLIMBING(124, "Recreation > Climbing"),
        RECREATION_COLLECTING(125, "Recreation > Collecting"),
        RECREATION_FOOD(126, "Recreation > Food"),
        RECREATION_GUNS(127, "Recreation > Guns"),
        RECREATION_HUMOR(128, "Recreation > Humor"),
        RECREATION_KITES(129, "Recreation > Kites"),
        RECREATION_LIVING_HISTORY(130, "Recreation > Living History"),
        RECREATION_MODELS(131, "Recreation > Models"),
        RECREATION_MOTORCYCLES(132, "Recreation > Motorcycles"),
        RECREATION_OUTDOORS(133, "Recreation > Outdoors"),
        RECREATION_PETS(134, "Recreation > Pets"),
        RECREATION_ROADS_AND_HIGHWAYS(135, "Recreation > Roads and Highways"),
        RECREATION_SCOUTING(136, "Recreation > Scouting"),
        RECREATION_THEME_PARKS(137, "Recreation > Theme Parks"),
        RECREATION_TOBACCO(138, "Recreation > Tobacco"),
        RECREATION_TRAINS_AND_RAILROADS(139, "Recreation > Trains and Railroads"),
        REFERENCE_ARCHIVES(140, "Reference > Archives"),
        REFERENCE_DICTIONARIES(141, "Reference > Dictionaries"),
        REFERENCE_EDUCATION(142, "Reference > Education"),
        REFERENCE_KNOWLEDGE_MANAGEMENT(143, "Reference > Knowledge Management"),
        REFERENCE_LIBRARIES(144, "Reference > Libraries"),
        REFERENCE_MAPS(145, "Reference > Maps"),
        REFERENCE_MUSEUMS(146, "Reference > Museums"),
        REFERENCE_QUOTATIONS(147, "Reference > Quotations"),
        SCIENCE_AGRICULTURE(148, "Science > Agriculture"),
        SCIENCE_ANOMALIES_AND_ALTERNATIVE_SCIENCE(149, "Science > Anomalies and Alternative Science"),
        SCIENCE_ASTRONOMY(150, "Science > Astronomy"),
        SCIENCE_BIOLOGY(151, "Science > Biology"),
        SCIENCE_CHEMISTRY(152, "Science > Chemistry"),
        SCIENCE_EARTH_SCIENCES(153, "Science > Earth Sciences"),
        SCIENCE_EDUCATIONAL_RESOURCES(154, "Science > Educational Resources"),
        SCIENCE_ENVIRONMENT(155, "Science > Environment"),
        SCIENCE_INSTRUMENTS_AND_SUPPLIES(156, "Science > Instruments and Supplies"),
        SCIENCE_MATH(157, "Science > Math"),
        SCIENCE_PHYSICS(158, "Science > Physics"),
        SCIENCE_SCIENCE_IN_SOCIETY(159, "Science > Science in Society"),
        SCIENCE_SOCIAL_SCIENCES(160, "Science > Social Sciences"),
        SCIENCE_TECHNOLOGY(161, "Science > Technology"),
        SHOPPING_ANTIQUES_AND_COLLECTIBLES(162, "Shopping > Antiques and Collectibles"),
        SHOPPING_AUCTIONS(163, "Shopping > Auctions"),
        SHOPPING_CHILDREN(164, "Shopping > Children"),
        SHOPPING_CLASSIFIEDS(165, "Shopping > Classifieds"),
        SHOPPING_CLOTHING(166, "Shopping > Clothing"),
        SHOPPING_CONSUMER_ELECTRONICS(167, "Shopping > Consumer Electronics"),
        SHOPPING_CRAFTS(168, "Shopping > Crafts"),
        SHOPPING_ENTERTAINMENT(169, "Shopping > Entertainment"),
        SHOPPING_ETHNIC_AND_REGIONAL(170, "Shopping > Ethnic and Regional"),
        SHOPPING_FOOD(171, "Shopping > Food"),
        SHOPPING_GENERAL_MERCHANDISE(172, "Shopping > General Merchandise"),
        SHOPPING_GIFTS(173, "Shopping > Gifts"),
        SHOPPING_HEALTH(174, "Shopping > Health"),
        SHOPPING_HOME_AND_GARDEN(175, "Shopping > Home and Garden"),
        SHOPPING_JEWELRY(176, "Shopping > Jewelry"),
        SHOPPING_NICHE(177, "Shopping > Niche"),
        SHOPPING_PETS(178, "Shopping > Pets"),
        SHOPPING_PHOTOGRAPHY(179, "Shopping > Photography"),
        SHOPPING_PUBLICATIONS(180, "Shopping > Publications"),
        SHOPPING_RECREATION(181, "Shopping > Recreation"),
        SHOPPING_SPORTS(182, "Shopping > Sports"),
        SHOPPING_TOOLS(183, "Shopping > Tools"),
        SHOPPING_TOYS_AND_GAMES(184, "Shopping > Toys and Games"),
        SHOPPING_VEHICLES(185, "Shopping > Vehicles"),
        SHOPPING_VISUAL_ARTS(186, "Shopping > Visual Arts"),
        SOCIETY_ACTIVISM(187, "Society > Activism"),
        SOCIETY_CRIME(188, "Society > Crime"),
        SOCIETY_DISABLED(189, "Society > Disabled"),
        SOCIETY_ETHNICITY(190, "Society > Ethnicity"),
        SOCIETY_FUTURE(191, "Society > Future"),
        SOCIETY_GAY_LESBIAN_AND_BISEXUAL(192, "Society > Gay, Lesbian, and Bisexual"),
        SOCIETY_GENEALOGY(193, "Society > Genealogy"),
        SOCIETY_GOVERNMENT(194, "Society > Government"),
        SOCIETY_HISTORY(195, "Society > History"),
        SOCIETY_HOLIDAYS(196, "Society > Holidays"),
        SOCIETY_ISSUES(197, "Society > Issues"),
        SOCIETY_LAW(198, "Society > Law"),
        SOCIETY_LIFESTYLE_CHOICES(199, "Society > Lifestyle Choices"),
        SOCIETY_MILITARY(200, "Society > Military"),
        SOCIETY_ORGANIZATIONS(201, "Society > Organizations"),
        SOCIETY_PARANORMAL(202, "Society > Paranormal"),
        SOCIETY_PEOPLE(203, "Society > People"),
        SOCIETY_PHILANTHROPY(204, "Society > Philanthropy"),
        SOCIETY_PHILOSOPHY(205, "Society > Philosophy"),
        SOCIETY_POLITICS(206, "Society > Politics"),
        SOCIETY_RELATIONSHIPS(207, "Society > Relationships"),
        SOCIETY_RELIGION_AND_SPIRITUALITY(208, "Society > Religion and Spirituality"),
        SOCIETY_SEXUALITY(209, "Society > Sexuality"),
        SOCIETY_SUBCULTURES(210, "Society > Subcultures"),
        SOCIETY_SUPPORT_GROUPS(211, "Society > Support Groups"),
        SOCIETY_TRANSGENDERED(212, "Society > Transgendered"),
        SOCIETY_WORK(213, "Society > Work"),
        SPORTS_ADVENTURE_RACING(214, "Sports > Adventure Racing"),
        SPORTS_BASEBALL(215, "Sports > Baseball"),
        SPORTS_BASKETBALL(216, "Sports > Basketball"),
        SPORTS_BOWLING(217, "Sports > Bowling"),
        SPORTS_BOXING(218, "Sports > Boxing"),
        SPORTS_CHEERLEADING(219, "Sports > Cheerleading"),
        SPORTS_CRICKET(220, "Sports > Cricket"),
        SPORTS_CYCLING(221, "Sports > Cycling"),
        SPORTS_DISABLED(222, "Sports > Disabled"),
        SPORTS_EQUESTRIAN(223, "Sports > Equestrian"),
        SPORTS_FANTASY(224, "Sports > Fantasy"),
        SPORTS_GOLF(225, "Sports > Golf"),
        SPORTS_HOCKEY(226, "Sports > Hockey"),
        SPORTS_LACROSSE(227, "Sports > Lacrosse"),
        SPORTS_MARTIAL_ARTS(228, "Sports > Martial Arts"),
        SPORTS_MOTORSPORTS(229, "Sports > Motorsports"),
        SPORTS_PAINTBALL(230, "Sports > Paintball"),
        SPORTS_RESOURCES(231, "Sports > Resources"),
        SPORTS_RODEO(232, "Sports > Rodeo"),
        SPORTS_RUNNING(233, "Sports > Running"),
        SPORTS_SKATEBOARDING(234, "Sports > Skateboarding"),
        SPORTS_SOCCER(235, "Sports > Soccer"),
        SPORTS_TENNIS(236, "Sports > Tennis"),
        SPORTS_TRACK_AND_FIELD(237, "Sports > Track and Field"),
        SPORTS_VOLLEYBALL(238, "Sports > Volleyball"),
        SPORTS_WATER_SPORTS(239, "Sports > Water Sports");
        
        private int topicId;
        private String query;

        private Odp239Topic(int topicId, String query)
        {
            this.topicId = topicId;
            this.query = query;
        }

        public int getTopicId()
        {
            return topicId;
        }

        @Override
        public String toString()
        {
            return query;
        }
    }

    @Override
    public void process() throws ProcessingException
    {
        query = topic.query;
        processInternal(DATA, topic.getTopicId(), results);
    }

    public static String getTopicLabel(String topicId)
    {
        return DATA.getTopicLabel(topicId);
    }
}
