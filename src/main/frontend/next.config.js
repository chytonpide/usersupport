/** @type {import('next').NextConfig} */

const API_KEY = process.env.API_KEY;

module.exports = {
  reactStrictMode: true,
  swcMinify: true,
  env: {
    API_ORIGIN: process.env.API_ORIGIN,
  },
  async rewrites () {
    return [{
      source:"/api/movies",
      destination:`https://api.themoviedb.org/3/movie/popular?api_key=${API_KEY}`,
    }]
  }
};
