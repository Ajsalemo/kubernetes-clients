package models

type Containers struct {
	Name     string   `json:"name"`
	Image    string   `json:"image"`
	Requests Requests `json:"requests"`
	Ports    []Ports  `json:"ports"`
}
