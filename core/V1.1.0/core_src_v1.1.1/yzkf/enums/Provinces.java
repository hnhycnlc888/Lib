package yzkf.enums;

public enum Provinces {
	/** 广东(guǎngdōng) */	GuangDong("1"),
	/** 云南(yúnnán) */YunNan("2"),
	/** 广西(guǎngxī) */GuangXi("3"),
	/** 上海(shànghǎi) */Shanghai("4"),
	/** 山东(shāndōng) */ShanDong("5"),
	/** 北京(běijīng) */BeiJing("6"),
	/** 辽宁(liáoníng) */LiaoNing("7"),
	/** 福建(fújiàn) */FuJian("8"),
	/** 黑龙江(hēilóngjiāng) */HeiLongJiang("9"),
	/** 贵州(guìzhōu) */GuiZhou("10"),
	/** 安徽(ānhuī) */AnHui("11"),
	/** 甘肃(gānsù) */GanSu("12"),
	/** 海南(hǎinán) */HaiNan("13"),
	/** 河北(héběi) */HeBei("14"),
	/** 河南(hénán) */HeNan("15"),
	/** 湖北(húběi) */HuBei("16"),
	/** 湖南(húnán) */HuNan("17"),
	/** 吉林(jílín) */JiLin("18"),
	/** 江苏(jiāngsū) */JiangSu("19"),
	/** 江西(jiāngxī) */JiangXi("20"),
	/** 内蒙古(nèiménggǔ) */NeiMengGu("21"),
	/** 宁夏(níngxià) */NingXia("22"),
	/** 青海(qīnghǎi) */QingHai("23"),
	/** 山西(shānxī) */ShanXi("24"),
	/** 陕西陕西(shǎnxī) */	Shanxi("25"),
	/** 四川(sìchuān) */SiChuan("26"),
	/** 天津(tiānjīn) */TianJin("27"),
	/** 西藏(xīzàng) */XiZang("28"),
	/** 新疆(xīnjiāng) */XinJiang("29"),
	/** 浙(zhè)江(jiāng) */ZheJiang("30"),
	/** 重(chóng)庆(qìng) */ChongQing("31");
	
	private String value;
	Provinces(String value){
		this.value = value;
	}
	public String getValue(){
		return this.value;
	}
}
