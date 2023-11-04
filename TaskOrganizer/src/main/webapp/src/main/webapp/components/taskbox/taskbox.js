const template = document.createElement("template");
template.innerHTML = `
    <link rel="stylesheet" type="text/css" href="${import.meta.url.match(/.*\//)[0]}/taskbox.css"/>

    <dialog>
       <!-- Modal content -->
        <span>&times;</span>
        <div>
            <div>Title:</div><div><input type="text" size="25" maxlength="80" placeholder="Task title" autofocus/></div>
            <div>Status:</div><div><select></select></div>
        </div>
        <p><button type="submit">Add task</button></p>
     </dialog>`;

/**
  * TaskBox
  * Manage view to add a new task
  */
class TaskBox extends HTMLElement {
	allStatuses = [];
	#dialog;
	#callback;
	#lastClickListener;
	constructor() {
		super();
		this.attachShadow({ mode: "open" });
		const copy = template.content.cloneNode(true);
		this.#dialog = copy.querySelector("dialog");
		this.shadowRoot.appendChild(copy);
	}

	/**
	 * Opens the modal box of view
	 * @public
	 */
	show() {
		//The DOM Dialog show() method 
		this.#dialog.show();

		const select = this.shadowRoot.querySelector("select");
		const closeButton = this.shadowRoot.querySelector("span");
		const button = this.shadowRoot.querySelector("button");
		//statuses section
		// Populate statuses if the select element is empty
		if (select.options.length === 0) {
			this.allStatuses.forEach(status => {
				select.add(new Option(status, status));
			});
		}
		//close button, and clear eventlistener if this existed before
		closeButton.removeEventListener("click", this.#lastClickListener);
		this.#lastClickListener = () => this.close();
		closeButton.addEventListener("click", this.#lastClickListener);
		//submit button
		// Remove any existing click event listeners
		button.removeEventListener("click", this.#lastClickListener);
		// Create and attach the new click event listener
		this.#lastClickListener = () => {
			const titleInput = this.shadowRoot.querySelector("input").value.trim();
			if (titleInput) {
				console.log(titleInput);
				this.newtaskCallback(titleInput, select.value);
				this.close();
			}
		};
		button.addEventListener("click", this.#lastClickListener);
	}

	/**
	 * Set the list of possible task states
	 * @public
	 * @param{Array<Object>} statuslist
	 */
	setStatuseslist(statuslist) {
		this.allStatuses = statuslist;
	}

	/**
	 * Add callback to run at click on the "Add task" button
	 * @public
	 * @param {function} callback
	 */
	newtaskCallback(callback) {
		this.#callback = callback;
	}

	/**
	 * Closes the modal box
	 * @public
	 */
	close() {
		this.#dialog.close();
	}
}

customElements.define('task-box', TaskBox);
