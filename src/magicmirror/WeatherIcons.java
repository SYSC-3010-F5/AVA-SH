/**
*Class:             WeatherIcons.java
*Project:          	AVA Smart Home
*Author:            Jason Van Kerkhoven                                             
*Date of Update:    31/03/2017                                              
*Version:           1.0.0
*                                                                                   
*Purpose:           A bunch of ASCII art.
*					All art is from github user "   szantaii"    
*					@ URL https://raw.githubusercontent.com/szantaii/bash-weather/master/init.sh
*
*					This class is merely a compelation of that art
*					
* 
*Update Log			v1.0.0
*						- null
*
*/
package magicmirror;

public abstract class WeatherIcons 
{
	//ASCII art for disconnected
	public static final String DISCONNECTED = 
			 "   ______  _________ _______  _______  _______  _        _        _______  _______ _________ _______  ______   \n" +
			 "   (  __  \\ \\__   __/(  ____ \\(  ____ \\(  ___  )( (    /|( (    /|(  ____ \\(  ____ \\__   __/(  ____ \\(  __  \\ \n" +
			 "   | (  \\  )   ) (   | (    \\/| (    \\/| (   ) ||  \\  ( ||  \\  ( || (    \\/| (    \\/   ) (   | (    \\/| (  \\  )\n" +
			 "   | |   ) |   | |   | (_____ | |      | |   | ||   \\ | ||   \\ | || (__    | |         | |   | (__    | |   ) |\n" +
			 "   | |   | |   | |   (_____  )| |      | |   | || (\\ \\) || (\\ \\) ||  __)   | |         | |   |  __)   | |   | |\n" +
			 "   | |   ) |   | |         ) || |      | |   | || | \\   || | \\   || (      | |         | |   | (      | |   ) |\n" +
			 "   | (__/  )___) (___/\\____) || (____/\\| (___) || )  \\  || )  \\  || (____/\\| (____/\\   | |   | (____/\\| (__/  )\n" +
			 "   (______/ \\_______/\\_______)(_______/(_______)|/    )_)|/    )_)(_______/(_______/   )_(   (_______/(______/ \n" ;
			                                                                                                             
	
	
	//ASCII art for a nice lovely lovely day!
	public static final String CLEAR_SKY_DAY =
	"                      oo                   \n"    +
	"         .            **            .      \n"    +
	"          *'.         **         .'*       \n"    +
	"           '*o.       **       .o*'        \n"    +
	"             '*'     .''.     '*'          \n"    +
	"                 .'********'.              \n"    +
	"                o************o             \n"    +
	"               o**************'            \n"    +
	"      'oooooo' **************** 'oooooo'   \n"    +
	"               '**************'            \n"    +
	"                '************'             \n"    +
	"                  ,o******o,               \n"    +
	"            .o*'      **      '*o.         \n"    +
	"          ,o*'        **        '*o,       \n"    +
	"        ,o            **            o,     \n"    +
	"                      oo                   \n"    ;
	
	
	//ASCII art for a nice lovely lovely 
	public static final String CLEAR_SKY_NIGHT = 
	"                    .,'o****o',.          \n"    +
	"                 ,'*********oo'''.        \n"    +
	"              .o********',                \n"    +
	"             '********,                   \n"    +
	"           .********o                     \n"    +
	"           ********o                      \n"    +
	"          '********                       \n"    +
	"          ********o                       \n"    +
	"          ********o                       \n"    +
	"          '********,                      \n"    +
	"           o********.                     \n"    +
	"            o********'                    \n"    +
	"             '********o,                  \n"    +
	"               'o********o',.     .       \n"    +
	"                 .,o************',        \n"    +
	"                     .'o***oo'.           \n"    ;
	
	
	//ASCII art for a slightly cloudy day! Delightful!
	public static final String FEW_CLOUDS_DAY =
	"                                           \n"    +
	"                                           \n"    +
	"               .,,,,.               |      \n"    +
	"            .'oo''''''',.       \\   |   /  \n"    +
	"          .oo,.       ..,'   ,   \\     /   \n"    +
	"         .*'              ,,,,    ,d8b,    \n"    +
	"         ,o                   --- 88888 ---\n"    +
	"     ,,,,',                       '98P'    \n"    +
	"   ,oo,                          /     \\   \n"    +
	"   o*,                          /   |   \\  \n"    +
	"   o*'                              |   '*o\n"    +
	"   .o*o,,............................,,o*o.\n"    +
	"     'o******',o**********o,'**********o'  \n"    +
	"                                           \n"    +
	"                                           \n"    ;
	
	
	//ASCII art for a slightly cloudy night! Spooky!
	public static final String FEW_CLOUDS_NIGHT =
	"                                           \n"    +
	"                                           \n"    +
	"               .,,,,.                ____  \n"    +
	"            .'oo''''''',.         ,''8L,-\\`.\n"    +
	"          .oo,.       ..,'   ,  ,'88,'     \n"    +
	"         .*'              ,,,, /888/       \n"    +
	"         ,o                   :888:        \n"    +
	"     ,,,,',                   :888:        \n"    +
	"   ,oo,                        \\888\\       \n"    +
	"   o*,                          \\`.88\\`._    \n"    +
	"   o*'                            \\`.9LL\\`-.'\n"    +
	"   .o*o,,............................,,o*o.\n"    +
	"     'o******',o**********o,'**********o'  \n"    +
	"                                           \n"    +
	"                                           \n"    ;
	
	
	//ASCII art for a cloudy
	public static final String CLOUDY =
	"                                           \n"    +
	"                                           \n"    +
	"               .,,,,.                      \n"    +
	"            .'oo''''''',.                  \n"    +
	"          .oo,.       ..,'   ,,',.         \n"    +
	"         .*'              ,,,,,''oo.       \n"    +
	"         ,o                       ,o,      \n"    +
	"     ,,,,',                        .,''',  \n"    +
	"   ,oo,                                ,oo,\n"    +
	"   o*,                                  ,**\n"    +
	"   o*'                                  '*o\n"    +
	"   .o*o,,............................,,o*o.\n"    +
	"     'o******',o**********o,'**********o'  \n"    +
	"                                           \n"    +
	"                                           \n"    +
	"                                           \n"    ;
	
	
	//ASCII art for rain. Ick
	public static final String RAIN =
	"               .,,,,.                      \n"    +
	"            .'oo''''''',.                  \n"    +
	"          .oo,.       ..,'   ,,',.         \n"    +
	"         .*'              ,,,,,''oo.       \n"    +
	"         ,o                       ,o,      \n"    +
	"     ,,,,',                        .,''',  \n"    +
	"   ,oo,                                ,oo,\n"    +
	"   o*,                                  ,**\n"    +
	"   o*'                                  '*o\n"    +
	"   .o*o,,............................,,o*o.\n"    +
	"     'o******',o**********o,'**********o'  \n"    +
	"            ,           ,           ,      \n"    +
	"        .,o*'       .,o*'       .,o*'      \n"    +
	"      '*****'     '*****'     '*****'      \n"    +
	"      o*****.     o*****.     o*****.      \n"    +
	"       o**o,       o**o,       o**o,       \n"    ;
	
	
	//ASCII art for thunderstorms. Bring umbrellas
	public static final String THUNDERSTORM =
	"               .,,,,.                      \n"    +
	"            .'oo''''''',.                  \n"    +
	"          .oo,.       ..,'   ,,',.         \n"    +
	"         .*'              ,,,,,''oo.       \n"    +
	"         ,o                       ,o,      \n"    +
	"     ,,,,',                        .,''',  \n"    +
	"   ,oo,                                ,oo,\n"    +
	"   o*,                                  ,**\n"    +
	"   o*'         / '/         / '/        '*o\n"    +
	"   .o*o,,...  /_ /_  ....  /_ /_  ...,,o*o.\n"    +
	"     'o*****   / /'  ****   / /'  *****o'  \n"    +
	"              /_/_         /_/_            \n"    +
	"               //'          //'            \n"    +
	"              //           //              \n"    +
	"             /            /                \n"    +
	"                                           \n"    ;
	
	
	//ASCII art for snow, eh
	public static final String SNOW = 
	"               .,,,,.                      \n"    +
	"            .'oo''''''',.                  \n"    +
	"          .oo,.       ..,'   ,,',.         \n"    +
	"         .*'              ,,,,,''oo.       \n"    +
	"         ,o                       ,o,      \n"    +
	"     ,,,,',                        .,''',  \n"    +
	"   ,oo,                                ,oo,\n"    +
	"   o*,                                  ,**\n"    +
	"   o*'                                  '*o\n"    +
	"   .o*o,,............................,,o*o.\n"    +
	"     'o******',o**********o,'**********o'  \n"    +
	"          __/\\__              __/\\__       \n"    +
	"          \\_\\/_/    __/\\__    \\_\\/_/       \n"    +
	"          /_/\\_\\    \\_\\/_/    /_/\\_\\       \n"    +
	"            \\/      /_/\\_\\      \\/         \n"    +
	"                      \\/                   \n"    ;
	
	
	//ASCII art for fog
	public static final String FOG_OR_MIST =
	"         .''o****o',.                     \n"    +
	"       ,o*************'.             ..   \n"    +
	"      o***o',,...,'o****o',.     .,'o**o  \n"    +
	"      .''.           ,o***************o.  \n"    +
	"                       ..,'o*****oo',.    \n"    +
	"           .,,'',,.                       \n"    +
	"       .'o**********o',                   \n"    +
	"      o*****o',,''o*****o'.       .,o**o  \n"    +
	"      ,'',.         .'o****o'''''o****o,  \n"    +
	"                       .,,'o*****oo',.    \n"    +
	"           .,,',,,.                       \n"    +
	"       .'o**********o',                   \n"    +
	"      o*****o',,''o*****o'.       .,o**o  \n"    +
	"      ,'',.         .'o****o'''''o****o,  \n"    +
	"                       .'o**********'.    \n"    +
	"                          .'o***o',.      \n"    ;
}
