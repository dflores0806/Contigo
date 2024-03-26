package es.unex.spilab.contigo.model;

import com.google.gson.annotations.SerializedName;

public class Alert {

	@SerializedName("title")
	private String title = null;
	@SerializedName("description")
	private String description = null;

	public Alert() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Alert(String title, String description) {
		this.title = title;
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Alert alert = (Alert) o;
		return (this.title == null ? alert.title == null : this.title.equals(alert.title))
				&& (this.description == null ? alert.description == null : this.description.equals(alert.description));
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + (this.title == null ? 0 : this.title.hashCode());
		result = 31 * result + (this.description == null ? 0 : this.description.hashCode());
		return result;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class Alert {\n");

		sb.append("  title: ").append(title).append("\n");
		sb.append("  description: ").append(description).append("\n");
		sb.append("}\n");
		return sb.toString();
	}
}
