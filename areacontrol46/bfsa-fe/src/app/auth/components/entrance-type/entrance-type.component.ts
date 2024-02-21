import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-entrance-type',
  templateUrl: './entrance-type.component.html',
  styleUrls: ['./entrance-type.component.scss']
})
export class EntranceTypeComponent implements OnInit {

  constructor(public router: Router) { }

  ngOnInit(): void {
  }

}
