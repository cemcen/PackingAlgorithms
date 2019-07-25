package graph_test

import algorithms.geometric.Container2D
import geometry.{Point, Polygon}
import network.{Graph, Node}
import org.junit.runner.RunWith
import org.scalatest.{FlatSpec, Matchers}
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class NetworkTest extends FlatSpec with Matchers {

  "A Network" should "know how to add a container" in {
    val graph: Graph = new Graph()
    val container: Container2D = Container2D(100, 50)

    graph.addContainer(container)

    graph.containsNode(Node(new Point(0, 0))) should be (true)
    graph.containsNode(Node(new Point(0, 50))) should be (true)
    graph.containsNode(Node(new Point(100, 50))) should be (true)
    graph.containsNode(Node(new Point(100, 0))) should be (true)
  }

  it should "pass test 1" in {

    var graph: Graph = new Graph()
    val container: Container2D = Container2D(100, 50)
    val polygonA: Polygon = new Polygon(List(new Point(0, 5.837241792),
      new Point(9.2816174376, 0),
      new Point(17.5343902768, 4.6366721235),
      new Point(18.0580172592, 14.4290245643),
      new Point(10.0211926458, 19.9550727638),
      new Point(0.3858565724, 14.9157680912)
    ))

    val polygonB: Polygon = new Polygon(List(new Point(17.5949808957, 5.7697777783),
      new Point(25.7220092518, 0),
      new Point(34.7420753178, 4.0414899689),
      new Point(35.8663701582, 13.8992058434),
      new Point(27.6804938753, 19.9038196867),
      new Point(19.2417229201, 16.640877315)
    ))

    graph.addContainer(container)
    graph = graph.addPolygon1Intersection(polygonA, container.getPolygon)
    graph = graph.addPolygon2Intersections(polygonB, container.getPolygon, polygonA)

    graph.exportPNGGraph(50, 100, "debug/test/graph_test/test_2/", "graph.png")

    val areaHole: Double = graph.calculateHoleArea(polygonB, container.getPolygon)
    areaHole should === (42.8935 +- 1e-4)
  }

  it should "The route of a hole should be correct" in {

    var graph: Graph = new Graph()
    val container: Container2D = Container2D(40, 20)
    graph.addContainer(container)

    val polygonA: Polygon = new Polygon(List(new Point(9.873203747009304,18.87967522412119),
      new Point(18.576790851313625,11.549736213083712),
      new Point(13.439021221975826,0.0),
      new Point(2.4774599942431497,1.2827299365263984),
      new Point(0.0,13.429938314738614)
    ))

    val polygonB: Polygon = new Polygon(List(new Point(26.480190456396482,0.0),
      new Point(18.676128438043282,4.477967963175011),
      new Point(18.524729770117293,15.255925513434633),
      new Point(26.045250511046333,19.937347247568322),
      new Point(35.629424398448144,15.077304152183153),
      new Point(35.38248097088226,4.497220715570403)
    ))

    graph = graph.addPolygon1Intersection(polygonA, container.getPolygon)
    graph = graph.addPolygon2Intersections(polygonB, container.getPolygon, polygonA)

    graph.exportPNGGraph(20, 40, "debug/test/graph_test/test_3/", "graph.png")

    val route = graph.lookForShortestRoute(
      graph.getNodeByPoint(new Point(0.0,13.429938314738614)),
      graph.getNodeByPoint(new Point(9.873203747009304,18.87967522412119))
    )

    val route2 = graph.lookForShortestRoute(
      graph.getNodeByPoint(new Point(9.873203747009304,18.87967522412119)),
      graph.getNodeByPoint(new Point(0.0,13.429938314738614))
    )

    graph.exportPNGRoute(20, 40, "debug/test/graph_test/test_3/", "route2.png", route2)

    route.distinct.length should === (11)
    route2.distinct.length should === (5)

    graph.getPolygonInGraph()
  }

  it should "it should find the correct holes (Container cw error) (Graph 1)" in {

    var graph: Graph = new Graph()
    val container: Container2D = Container2D(50, 50)
    graph.addContainer(container)

    val polygonA: Polygon = new Polygon(List(new Point(25.009771483112065,2.998712319330295),
      new Point(29.680474897306794,16.256907321448658),
      new Point(20.122892114600255,28.072282969822055),
      new Point(4.947623285251153,25.300816713567258),
      new Point(0.0,11.890919459601822),
      new Point(9.542436253995271,0.0)
    ))

    val polygonB: Polygon = new Polygon(List(new Point(19.38181701665519,39.0844692643667),
      new Point(7.8365197894500085,46.12946215677053),
      new Point(3.552713678800501E-15,34.19507813253752),
      new Point(12.302696434326421,26.644077071863144)
    ))

    val polygonC: Polygon = new Polygon(List(new Point(27.356295257807812,9.659522699084789),
      new Point(38.05488497660717,0.0),
      new Point(47.349850599115264,9.804331423452119),
      new Point(37.86227699705536,19.962155611049738)
    ))

    val polygonD: Polygon = new Polygon(List(new Point(50.00000000000001,33.88201640308741),
      new Point(35.71064651419611,33.721694626521845),
      new Point(35.49094167746731,20.1274735760122),
      new Point(49.37799313948949,19.15933589085798)
    ))

    val polygonE: Polygon = new Polygon(List(new Point(35.04618569254171,47.90867216691592),
      new Point(21.012190873257257,49.999999999999986),
      new Point(18.930703370051607,36.06421421376885),
      new Point(32.54550964970839,33.66861394420066)
    ))

    graph = graph.addPolygon1Intersection(polygonA, container.getPolygon)
    graph = graph.addPolygon2Intersections(polygonB, container.getPolygon, polygonA)
    graph = graph.addPolygon2Intersections(polygonC, container.getPolygon, polygonA)
    graph = graph.addPolygon2Intersections(polygonD, container.getPolygon, polygonC)
    graph = graph.addPolygon2Intersections(polygonE, container.getPolygon, polygonB)

    graph.exportPNGGraph(50, 50, "debug/test/graph_test/test_4/", "graph.png")

    val route = graph.lookForShortestRoute(
      graph.getNodeByPoint(new Point(47.349850599115264,9.804331423452119)),
      graph.getNodeByPoint(new Point(38.05488497660717,0.0))
    )

    graph.exportPNGRoute(50, 50, "debug/test/graph_test/test_4/", "route1.png", route)

    val route2 = graph.lookForShortestRoute(
      graph.getNodeByPoint(new Point(18.930703370051607,36.06421421376885)),
      graph.getNodeByPoint(new Point(32.54550964970839,33.66861394420066))
    )

    val route3 = graph.lookForShortestRoute(
      graph.getNodeByPoint(new Point(35.71064651419611,33.721694626521845)),
      graph.getNodeByPoint(new Point(35.49094167746731,20.1274735760122))
    )

    val route4 = graph.lookForShortestRoute(
      graph.getNodeByPoint(new Point(37.86227699705536,19.962155611049738)),
      graph.getNodeByPoint(new Point(49.37799313948949,19.15933589085798))
    )

    route.distinct.length should === (6)
    route2.length should === (6)
    route3.length should === (6)
    route4.length should === (6)

    graph.getNumberOfEdges should === (36)
    graph.getNumberOfNodes should === (26)

    val routes = graph.getPolygonInGraph(drawRoutes = true, "debug/test/graph_test/test_4/", 50, 50)
    routes.length should === (12)
  }

  it should "it should find the correct holes (Colinnear error) (Graph 2)" in {

    var graph: Graph = new Graph()
    val container: Container2D = Container2D(70, 70)
    graph.addContainer(container)

    val polygon1: Polygon = new Polygon(List(new Point(0.0,14.899281817577759),
      new Point(1.9204833637853982,0.0),
      new Point(15.13981670384878,1.832161575498537),
      new Point(13.749938138835933,16.10600156267335)
    ))

    val polygon2: Polygon = new Polygon(List(new Point(-1.7763568394002505E-15,26.786374900245136),
      new Point(7.5334022349773395,15.56042699022535),
      new Point(19.668497838587605,23.206151854132997),
      new Point(11.808423917503829,35.093233537675545)
    ))

    val polygon3: Polygon = new Polygon(List(new Point(-1.7763568394002505E-15,42.801044038486616),
      new Point(11.585236879022185,34.93622840377063),
      new Point(19.616323133791035,46.699386536364436),
      new Point(7.785484633934816,54.570495451519385)
    ))

    val polygon4: Polygon = new Polygon(List(new Point(14.29343893013365,53.86938530346488),
      new Point(16.1924163812369,67.12748398324473),
      new Point(2.066670822585266,69.67993308051575),
      new Point(0.0,55.40923555876974)
    ))

    val polygon5: Polygon = new Polygon(List(new Point(27.621497302774422,29.77845739826317),
      new Point(15.387555931108114,19.66841010533436),
      new Point(17.911065260891025,5.52727975818793),
      new Point(31.242269193491538,0.0),
      new Point(43.633021344512535,9.577860397845285),
      new Point(41.093314419290905,24.565685332240598)
    ))

    val polygon6: Polygon = new Polygon(List(new Point(16.125750839478005,49.02277716935105),
      new Point(29.607441238949303,40.04911023170012),
      new Point(41.857511663980404,46.03974202820418),
      new Point(42.916274586520856,62.43676945385946),
      new Point(31.048588743034756,70.0),
      new Point(18.180621847082588,64.45566225478842)
    ))

    val polygon7: Polygon = new Polygon(List(new Point(34.90832872943605,38.3084493994646),
      new Point(38.837093386071594,25.43870534691397),
      new Point(53.34091269852139,21.23769155688624),
      new Point(64.01013443980764,31.307028281064028),
      new Point(60.66585890679011,46.01728437840142),
      new Point(45.61763917712107,50.223060613897935)
    ))

    val polygon8: Polygon = new Polygon(List(new Point(12.696441959465034,33.973544090690126),
      new Point(23.111652564825725,26.051551881940824),
      new Point(32.45820220621048,36.72616706366023),
      new Point(20.826719516811742,45.89372391677252)
    ))

    val polygon9: Polygon = new Polygon(List(new Point(53.09022515331643,1.621259051058928),
      new Point(63.37512982609044,11.646122262848507),
      new Point(53.73612424753197,21.61068203930092),
      new Point(43.39553362916449,10.97937025961616)
    ))

    graph = graph.addPolygon1Intersection(polygon1, container.getPolygon)
    graph = graph.addPolygon2Intersections(polygon2, container.getPolygon, polygon1)
    graph = graph.addPolygon2Intersections(polygon3, container.getPolygon, polygon2)
    graph = graph.addPolygon2Intersections(polygon4, container.getPolygon, polygon3)
    graph = graph.addPolygon2Intersections(polygon5, container.getPolygon, polygon2)
    graph = graph.addPolygon2Intersections(polygon6, container.getPolygon, polygon3)
    graph = graph.addPolygon2Intersections(polygon7, polygon5, polygon6)
    graph = graph.addPolygon2Intersections(polygon8, polygon5, polygon6)
    graph = graph.addPolygon2Intersections(polygon9, polygon5, polygon7)

    graph.exportPNGGraph(70, 70, "debug/test/graph_test/test_5/", "graph.png", circle_size = (15,15))

    val routes = graph.getPolygonInGraph(drawRoutes = true, "debug/test/graph_test/test_5/", 70, 70, circle_size = (15,15))

    routes.length should === (20)
  }

  it should "it should find the correct holes (Holes are not calculated) (Graph 3)" in {

    var graph: Graph = new Graph()
    val container: Container2D = Container2D(150, 150)
    graph.addContainer(container)

    val polygon1: Polygon = new Polygon(List(new Point(8.472220022300762, 26.244579098372178),
        new Point(0.0, 14.204645858612693),
        new Point(6.704974212172571, 0.15594761558310566),
        new Point(22.9012526574189, 0.0),
        new Point(29.923815740527104, 12.542492581317024),
        new Point(23.588379536808752, 24.948326879625895),
    ))

    val polygon2: Polygon = new Polygon(List(new Point( 26.8987546595802,47.86036340168522),
      new Point( 14.316708579390564,55.818965429427195),
      new Point( 1.1678127355455636,49.157977902826794),
      new Point( 0.0,34.606548383432525),
      new Point( 11.559092850495155,25.97987127038887),
      new Point( 25.792648954201354,32.03257701404585),
    ))

    val polygon3: Polygon = new Polygon(List(new Point( -3.552713678800501E-15,76.72451744991476),
      new Point( 0.511570918337906,62.395017185053646),
      new Point( 12.785730625553533,55.043400304026775),
      new Point( 25.92750470198829,61.7448302021177),
      new Point( 26.795221027441293,76.82603908964671),
      new Point( 14.62615448858265,84.98153291949407),
    ))

    val polygon4: Polygon = new Polygon(List(new Point( 6.526368271096043,80.59077628223233),
      new Point( 19.050187566207274,87.37972616527499),
      new Point( 12.077811780355628,99.80262389936416),
      new Point( -3.552713678800501E-15,93.44598364908047),
    ))

    val polygon5: Polygon = new Polygon(List(new Point( 26.619044221336427,4.791825586591358),
      new Point( 40.7447309105237,0.0),
      new Point( 44.50051366378862,13.672374127553582),
      new Point( 32.27371427989179,18.053699920234124),
    ))

    val polygon6: Polygon = new Polygon(List(new Point( 12.101961958789785,99.78745377503965),
      new Point( 19.66152097729063,110.99511971099332),
      new Point( 7.4671561489975655,119.24299068810245),
      new Point( 5.329070518200751E-15,107.38939626113918),
    ))

    val polygon7: Polygon = new Polygon(List(new Point( 19.96079928694388,127.95157748824562),
      new Point( 9.408578435024566,137.6554108439891),
      new Point( 0.0,128.50991727039607),
      new Point( 9.789209873239656,117.67242922064261),
    ))

    val polygon8: Polygon = new Polygon(List(new Point( 49.92366127491492,4.263256414560601E-14),
      new Point( 62.995942407816536,5.833955706433173),
      new Point( 57.435188610414095,18.535286589958588),
      new Point( 44.29306303860842,12.91718067074548),
    ))

    val polygon9: Polygon = new Polygon(List(new Point( 81.72115039650633,12.262598735760506),
      new Point( 70.05076055335415,19.752068589638398),
      new Point( 62.34187673354108,7.327908657993262),
      new Point( 73.06305500994831,-4.263256414560601E-14),
    ))

    val polygon10: Polygon = new Polygon(List(new Point( 92.63577931177181,-2.8421709430404007E-14),
      new Point( 106.16822724550916,4.032711612212012),
      new Point( 110.09514109878775,18.64148588746056),
      new Point( 99.62679682887078,29.155273332489443),
      new Point( 85.01574442053774,25.306290153784275),
      new Point( 81.25119116264133,10.401973972626507),
    ))

    val polygon11: Polygon = new Polygon(List(new Point( 111.19020919976653,24.251672812661784),
      new Point( 108.525668542541,10.60085863638145),
      new Point( 121.00950954726778,-2.4868995751603507E-14),
      new Point( 133.4557457482689,4.22058328194149),
      new Point( 137.37461934566522,18.82377093780949),
      new Point( 125.73137415383772,29.61089588597952),
    ))

    val polygon12: Polygon = new Polygon(List(new Point( 85.72165540320368,30.375452461765022),
      new Point( 72.11788291477176,33.66815930182413),
      new Point( 68.57249916896528,20.090624157827065),
      new Point( 82.87122699379852,16.815889409132538),
    ))

    val polygon13: Polygon = new Polygon(List(new Point( 27.71824166633649,32.315080626940755),
      new Point( 30.52375988154966,18.619385500422315),
      new Point( 45.959641615152854,13.629623640078027),
      new Point( 56.603150664072665,24.216745134801894),
      new Point( 53.154951253502354,38.29380583305479),
      new Point( 39.83062429466917,42.9544367544554),
    ))

    val polygon14: Polygon = new Polygon(List(new Point( 136.85519337046202,16.888195624978863),
      new Point( 133.28919683678015,2.4438063833098234),
      new Point( 147.47365357176454,4.618527782440651E-14),
      new Point( 149.93148040300127,13.534423938297081),
    ))

    val polygon15: Polygon = new Polygon(List(new Point( 45.43911736615044,121.64992341988673),
      new Point( 33.19339024293538,132.67283408886675),
      new Point( 19.15496052620523,127.13721743452047),
      new Point( 16.40097595986998,114.1146542103634),
      new Point( 27.84542290211141,103.16515132916251),
      new Point( 42.721320044451005,108.57311291300553),
    ))

    val polygon16: Polygon = new Polygon(List(new Point( 79.86413718993677,39.09197008362884),
      new Point( 78.85877629333382,53.0840526583535),
      new Point( 64.50984259625201,60.049309164743164),
      new Point( 53.43509073992769,53.117397763818246),
      new Point( 53.43188302650654,37.16324900797554),
      new Point( 67.1220969892311,30.170185106294866),
    ))

    val polygon17: Polygon = new Polygon(List(new Point( 132.99672125612915,33.27183914491534),
      new Point( 136.70684343916145,19.44244571637453),
      new Point( 149.99999999999994,22.747902443571355),
      new Point( 147.35851772315817,36.3383543381676),
    ))

    val polygon18: Polygon = new Polygon(List(new Point( 130.01657502378225,44.70297189638456),
      new Point( 138.99876840557573,34.55338973760397),
      new Point( 150.00000000000006,43.962443426895256),
      new Point( 140.31356037610567,54.49719409542892),
    ))

    val polygon19: Polygon = new Polygon(List(new Point( 139.56827265539758,53.788295977283326),
      new Point( 149.99999999999986,66.69065482690499),
      new Point( 144.32209343393637,79.90030568724058),
      new Point( 129.17781081429428,81.89265025772254),
      new Point( 120.42872000765067,71.37521718488986),
      new Point( 125.5826232713651,56.47494594833017),
    ))

    val polygon20: Polygon = new Polygon(List(new Point( 149.99999999999977,90.76185520377996),
      new Point( 146.06734223918016,106.09597909040214),
      new Point( 132.23308318011738,109.85945569482168),
      new Point( 121.10116084182096,98.74543828123251),
      new Point( 124.47703673943596,85.30180735541202),
      new Point( 138.90817855731325,80.61254701160024),
    ))

    val polygon21: Polygon = new Polygon(List(new Point( 137.1759607155978,108.51479358578699),
      new Point( 149.99999999999977,116.83758240104414),
      new Point( 149.79231698154229,130.57017379593913),
      new Point( 137.01382112190225,138.49540976902023),
      new Point( 124.32654951899423,132.17956185648995),
      new Point( 123.22159234600298,116.64391935940502),
    ))

    val polygon22: Polygon = new Polygon(List(new Point( 30.63617202101102,142.97600092070553),
      new Point( 17.59816012452886,148.01626875104847),
      new Point( 12.300513507550406,134.99598432011027),
      new Point( 25.342167872704053,129.57694956427247),
    ))

    val polygon23: Polygon = new Polygon(List(new Point( 102.3777300109177,43.13520339174784),
      new Point( 112.04799370555995,32.33145824655753),
      new Point( 128.00272747373035,36.29901950319305),
      new Point( 131.42698639517735,50.58873520538725),
      new Point( 121.1976683517349,60.89129863012789),
      new Point( 106.10768812804824,56.80703495504207),
    ))

    val polygon24: Polygon = new Polygon(List(new Point( 98.54628733985723,37.42320249883592),
      new Point( 92.20573957854194,49.23235290183557),
      new Point( 79.54627218729146,43.515847498849226),
      new Point( 85.19985126075997,30.50175184456164),
    ))

    val polygon25: Polygon = new Polygon(List(new Point( 19.8426806533284,85.29466743346914),
      new Point( 33.480755274658875,91.33561863185854),
      new Point( 28.4039079935675,103.36621338133851),
      new Point( 14.831224875519368,98.4798671511579),
    ))

    val polygon26: Polygon = new Polygon(List(new Point( 119.36102148140982,132.65824478529825),
      new Point( 132.481958866877,136.23940440718053),
      new Point( 129.2939068668445,150.00000000000003),
      new Point( 115.29459363739444,146.4586413768027),
    ))

    val polygon27: Polygon = new Polygon(List(new Point( 115.3954015978502,146.98854152837222),
        new Point( 102.1778042680005,149.99999999999986),
        new Point( 98.62016232909644,136.11172868042914),
        new Point( 112.74030553566364,133.03194723340297),
    ))

    val polygon28: Polygon = new Polygon(List(new Point( 75.6417872985629,124.34341925223296),
      new Point( 89.17079907468057,123.0432792886692),
      new Point( 98.6690581557519,136.30260750372068),
      new Point( 92.47964488858445,149.15599655524687),
      new Point( 76.19462770127488,150.0),
      new Point( 68.87036857889557,139.3521986340451),
    ))

    val polygon29: Polygon = new Polygon(List(new Point( 67.15751709156461,149.9999999999997),
      new Point( 53.04248819239626,147.67955735897235),
      new Point( 54.97070396426574,134.14760334381415),
      new Point( 69.38532296874297,136.15102673083194),
    ))

    val polygon30: Polygon = new Polygon(List(new Point( 91.19928838095733,125.87499879253556),
      new Point( 93.08434433871575,110.47095974151321),
      new Point( 106.7138432139299,104.84432228209718),
      new Point( 118.2572703943122,112.93879622735759),
      new Point( 117.93711899612354,127.12225829128693),
      new Point( 104.94524852145655,134.73214755146353),
    ))

    val polygon31: Polygon = new Polygon(List(new Point( 53.726177222843276,142.88152144637064),
      new Point( 41.39782404384164,149.99999999999977),
      new Point( 34.16108613498875,138.87037607760914),
      new Point( 46.12169705498424,130.5750517124205),
    ))

    val polygon32: Polygon = new Polygon(List(new Point( 50.90396406942179,137.24782674851366),
      new Point( 42.557330915118655,125.6016335980018),
      new Point( 53.963287854483795,117.483305761287),
      new Point( 62.332133239330446,128.5357186595906),
    ))

    val polygon33: Polygon = new Polygon(List(new Point( 55.58831766090621,107.65234542399303),
      new Point( 68.52718668624493,98.25160174343506),
      new Point( 81.83096912171557,104.65321588036436),
      new Point( 83.58123122835924,118.43452394234366),
      new Point( 70.47572437603485,128.18823826518138),
      new Point( 58.10743440182435,122.95632105145135),
    ))

    val polygon34: Polygon = new Polygon(List(new Point( 55.05197817333206,108.48280259278145),
      new Point( 41.75206708605522,108.2207521300641),
      new Point( 33.709941141027485,93.77381430715302),
      new Point( 42.186076156455314,81.38192910884898),
      new Point( 55.610691350356205,81.61365789287318),
      new Point( 63.665004077909785,95.1465682241935),
    ))

    val polygon35: Polygon = new Polygon(List(new Point( 58.462481716087794,56.264143557914664),
      new Point( 64.533817713657,69.49614007130674),
      new Point( 56.49421857962854,81.65990358594333),
      new Point( 41.30940025013797,80.86509792005397),
      new Point( 34.608507810205836,67.3887102603436),
      new Point( 41.81999035373855,55.51154599577977),
    ))

    val polygon36: Polygon = new Polygon(List(new Point( 62.384689103223025,92.99537440612762),
      new Point( 58.49120021670052,78.63850762185825),
      new Point( 71.06386500190025,74.98044248235294),
      new Point( 75.94471754599662,88.30985472511118),
    ))

    val polygon37: Polygon = new Polygon(List(new Point( 80.89402884246316,62.8886787972442),
      new Point( 76.60551953768181,76.40640108924714),
      new Point( 63.72872607397761,73.09299092442944),
      new Point( 67.71081033223302,58.49549605078896),
    ))

    val polygon38: Polygon = new Polygon(List(new Point( 93.79283355624956,66.42529852234219),
      new Point( 103.59496771769018,77.87475299476496),
      new Point( 99.08407187166017,91.54345413677262),
      new Point( 84.26531447924171,94.8694600456757),
      new Point( 74.06959649404429,83.18897445250822),
      new Point( 79.40173471385162,68.92765104617342),
    ))

    val polygon39: Polygon = new Polygon(List(new Point( 85.87518938002185,67.80203403810492),
      new Point( 75.73000942450854,57.79495381492328),
      new Point( 85.51501413970517,47.8053993472657),
      new Point( 95.67853619935998,56.78980167312401),
    ))

    val polygon40: Polygon = new Polygon(List(new Point( 111.54260725505492,74.31113816404466),
      new Point( 124.79624483606399,76.62549416702949),
      new Point( 123.15809923406871,90.5541654319955),
      new Point( 108.65191521650151,88.41608945068486),
    ))

    val polygon41: Polygon = new Polygon(List(new Point( 82.32795986017639,94.88646733216198),
      new Point( 96.50954041080878,94.76197272447409),
      new Point( 96.81613894429478,108.67132616043423),
      new Point( 82.37127711146549,108.90752203646983),
    ))

    val polygon42: Polygon = new Polygon(List(new Point( 33.33585194901484,45.98535707821797),
      new Point( 40.04116280525946,58.44123858868126),
      new Point( 27.854310882712625,65.21757980584279),
      new Point( 20.89212861406328,52.66939724530097),
    ))

    val polygon43: Polygon = new Polygon(List(new Point( 111.93182674085129,64.88980613187286),
      new Point( 100.25566795599548,72.77146831042657),
      new Point( 92.446970362401,60.41986338114484),
      new Point( 103.38404586882655,53.0271494453736),
    ))

    val polygon44: Polygon = new Polygon(List(new Point( 98.52992595479932,104.29945401111196),
      new Point( 99.56971193119372,90.07189082364641),
      new Point( 113.43167750998379,90.9613030990395),
      new Point( 112.60848197980535,105.23677512964547),
    ))

    graph = graph.addPolygon1Intersection(polygon1, container.getPolygon)
    graph = graph.addPolygon2Intersections(polygon2, container.getPolygon, polygon1)
    graph = graph.addPolygon2Intersections(polygon3, container.getPolygon, polygon2)
    graph = graph.addPolygon2Intersections(polygon4, container.getPolygon, polygon3)
    graph = graph.addPolygon2Intersections(polygon5, container.getPolygon, polygon1)
    graph = graph.addPolygon2Intersections(polygon6, container.getPolygon, polygon4)
    graph = graph.addPolygon2Intersections(polygon7, container.getPolygon, polygon6)
    graph = graph.addPolygon2Intersections(polygon8, container.getPolygon, polygon5)
    graph = graph.addPolygon2Intersections(polygon9, container.getPolygon, polygon8)
    graph = graph.addPolygon2Intersections(polygon10, container.getPolygon, polygon9)
    graph = graph.addPolygon2Intersections(polygon11, container.getPolygon, polygon10)
    graph = graph.addPolygon2Intersections(polygon12, polygon9, polygon10)
    graph = graph.addPolygon2Intersections(polygon13, polygon5, polygon8)
    graph = graph.addPolygon2Intersections(polygon14, container.getPolygon, polygon11)
    graph = graph.addPolygon2Intersections(polygon15, polygon6, polygon7)
    graph = graph.addPolygon2Intersections(polygon16, polygon12, polygon13)
    graph = graph.addPolygon2Intersections(polygon17, container.getPolygon, polygon11)
    graph = graph.addPolygon2Intersections(polygon18, container.getPolygon, polygon17)
    graph = graph.addPolygon2Intersections(polygon19, container.getPolygon, polygon18)
    graph = graph.addPolygon2Intersections(polygon20, container.getPolygon, polygon19)
    graph = graph.addPolygon2Intersections(polygon21, container.getPolygon, polygon20)
    graph = graph.addPolygon2Intersections(polygon22, polygon7, polygon15)
    graph = graph.addPolygon2Intersections(polygon23, polygon18, polygon19)
    graph = graph.addPolygon2Intersections(polygon24, polygon12, polygon16)
    graph = graph.addPolygon2Intersections(polygon25, polygon4, polygon15)
    graph = graph.addPolygon2Intersections(polygon26, container.getPolygon, polygon21)
    graph = graph.addPolygon2Intersections(polygon27, container.getPolygon, polygon26)
    graph = graph.addPolygon2Intersections(polygon28, container.getPolygon, polygon27)
    graph = graph.addPolygon2Intersections(polygon29, container.getPolygon, polygon28)
    graph = graph.addPolygon2Intersections(polygon30, polygon27, polygon28)
    graph = graph.addPolygon2Intersections(polygon31, container.getPolygon, polygon29)
    graph = graph.addPolygon2Intersections(polygon32, polygon29, polygon31)
    graph = graph.addPolygon2Intersections(polygon33, polygon28, polygon32)
    graph = graph.addPolygon2Intersections(polygon34, polygon15, polygon33)
    graph = graph.addPolygon2Intersections(polygon35, polygon16, polygon34)
    graph = graph.addPolygon2Intersections(polygon36, polygon34, polygon35)
    graph = graph.addPolygon2Intersections(polygon37, polygon16, polygon36)
    graph = graph.addPolygon2Intersections(polygon38, polygon36, polygon37)
    graph = graph.addPolygon2Intersections(polygon39, polygon37, polygon38)
    graph = graph.addPolygon2Intersections(polygon40, polygon20, polygon19)
    graph = graph.addPolygon2Intersections(polygon41, polygon33, polygon38)
    graph = graph.addPolygon2Intersections(polygon42, polygon3, polygon35)
    graph = graph.addPolygon2Intersections(polygon43, polygon23, polygon39)
    graph = graph.addPolygon2Intersections(polygon44, polygon30, polygon38)

    graph.exportPNGGraph(150, 150, "debug/test/graph_test/test_6/", "graph.png", circle_size = (10,10))

    graph.getPolygonInGraph(drawRoutes = true, "debug/test/graph_test/test_6/", 150, 150, circle_size = (10,10))
  }
}