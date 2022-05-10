import React from 'react';
import { Card, CardContent, CardMedia, Paper, Typography } from "@material-ui/core";

function App() {

  const state =
    new State(
      [
        new Item("Trapo de bienvenida", "$120", "http://hiperlimpieza.com.ar/1981-thickbox_dm/trapo-de-pisos-duramas-gris.jpg"),
        new Item("Foto en la playa", "$150", "nubeplayaigfeo.jpeg"),
        new Item("Piña", "$200", "https://http2.mlstatic.com/D_NQ_NP_745526-MLA45294786100_032021-O.webp"),
      ]
    )

  return (
    <div style={{display: "flex", flexDirection: "column", alignItems: "center"}}>
      <div>
        {state.items.map(ItemCard)}
      </div>
      {ItemList(state.items)}
    </div>
  );
}

class Item {
  constructor(
    readonly title: string,
    readonly price: string,
    readonly imageUrl: string = "",
  ) {
  }
}

const ItemCard = (item: Item) =>
  <Card style={{margin: 12, maxWidth: 240}}>
    <CardMedia
      style={{width: 240, height: 240}}
      image={item.imageUrl}
      title={item.title}
    />
    <CardContent>
      <Typography component="h5" variant="h5">
        {item.price}
      </Typography>
      <Typography variant="subtitle1" color="textSecondary">
        {item.title}
      </Typography>
    </CardContent>
  </Card>

const ItemList = (items: Item[]) =>
  <div style={{display: "flex", flexDirection: "column", alignItems: "stretch"}}>
    {items.map(ItemRow)}
  </div>

const ItemRow = (item: Item) =>
  <Card style={{margin: 2, display: "flex"}}>
    <CardMedia
      style={{width: 120, height: 120}}
      image={item.imageUrl}
      title={item.title}
    />
    <CardContent>
      <Typography component="h5" variant="h5">
        {item.price}
      </Typography>
      <Typography variant="subtitle1" color="textSecondary">
        {item.title}
      </Typography>
    </CardContent>
  </Card>

const ItemRenderer: Renderer<Item> = {
  render: item => `${item.title} ${item.price}`
}

class State {
  constructor(readonly items: Array<Item>) {
  }
}

const StateRenderer: Renderer<State> = {
  render: state => state.items.map((item) => ItemRenderer.render(item)).toString()
}

interface Renderer<A> {
  render(a: A): string
}

export default App;
