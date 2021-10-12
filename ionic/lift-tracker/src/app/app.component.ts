import { Component } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { PasteService } from './services/paste.service';

@Component({
	selector: 'app-root',
	templateUrl: 'app.component.html'
})
export class AppComponent {

	constructor(
		private pasteService: PasteService
	) { }

	ngOnInit() {

	}
}
