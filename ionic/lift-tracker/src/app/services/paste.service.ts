import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";

@Injectable()
export class PasteService {

	constructor(
		private http: HttpClient
	) {}

	getStore(): Promise<any> {
		return this.getData('store');
	}

	getBackup(): Promise<any> {
		return this.getData('backup');
	}

	getList(): Promise<any> {
		return new Promise<any>(resolve => {
			if (this.key) {
				this.http.get('https://api.paste.ee/v1/pastes?key=' + this.key).subscribe(res => {
					let data = res['data'];
					resolve(data);
				});
			} else {
				resolve(null);
			}
		});
	}

	getData(description: string): Promise<any> {
		return new Promise<any>(resolve => {
			if (this.key) {
				this.http.get('https://api.paste.ee/v1/pastes?key=' + this.key).subscribe(res => {
					let data = res['data'].find(d => d.description == description);
					if (data) {
						this.http.get('https://api.paste.ee/v1/pastes/' + data.id + '?key=' + this.key).subscribe(data => {
							let content = data['paste'].sections[0]?.contents;
							resolve(content);
						});
					} else {
						resolve(null);
					}
				});
			} else {
				resolve(null);
			}
		});
	}

	postData(description: string, content: string): Promise<any> {
		return new Promise<any>(resolve => {
			if (this.key) {
				this.removeAllData(description).then(() => {
					let body = {
						description: description,
						sections: [
							{
								contents: content
							}
						]
					};
					this.http.post('https://api.paste.ee/v1/pastes?key=' + this.key, body).subscribe(res => {
						resolve(res);
					});
				});
			} else {
				resolve(null);
			}
		});
	}

	removeAllData(description: string): Promise<void> {
		return new Promise<void>(resolve => {
			if (this.key) {
				this.getList().then(async list => {
					for (let item of list) {
						if (item.description == description) {
							await this.removeData(item.id);
						}
					}
					resolve()
				});
			} else {
				resolve();
			}
		});
	}

	removeData(id: string): Promise<void> {
		return new Promise<void>(resolve => {
			this.http.delete('https://api.paste.ee/v1/pastes/' + id + '?key=' + this.key).subscribe(res => {
				resolve();
			});
		});
	}

	get key() {
		return localStorage.getItem('pasteKey');
	}
}
