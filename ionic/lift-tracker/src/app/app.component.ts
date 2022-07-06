import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { App } from '@capacitor/app';
import { Device } from '@capacitor/device';
import { Observable } from 'rxjs';
import { AuthService } from './services/auth.service';

@Component({
	selector: 'app-root',
	templateUrl: 'app.component.html'
})
export class AppComponent implements OnInit {

	constructor(
		private http: HttpClient,
		private authService: AuthService
	) { }

	ngOnInit() {

	}
}
