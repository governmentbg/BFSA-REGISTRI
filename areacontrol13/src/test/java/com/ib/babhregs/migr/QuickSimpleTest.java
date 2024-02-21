package com.ib.babhregs.migr;

import com.ib.babhregs.migr.invitro.InvitroMigrate;

public class QuickSimpleTest {

	public static void main(String[] args) {
		
		
		String s = ", София,, ул. Иван Вазов 12,  ,, , ,  Стара Загора , ,";
		System.out.println(InvitroMigrate.clearParazites(s));
	
		
		
	}

}
