-- phpMyAdmin SQL Dump
-- version 5.0.2
-- https://www.phpmyadmin.net/
--
-- Gép: 127.0.0.1
-- Létrehozás ideje: 2020. Máj 13. 14:10
-- Kiszolgáló verziója: 10.4.11-MariaDB
-- PHP verzió: 7.4.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Adatbázis: `orvositorzsadat`
--

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `examine`
--

CREATE TABLE `examine` (
  `id` int(11) NOT NULL,
  `socialinsurancecode` varchar(9) COLLATE utf8mb4_hungarian_ci NOT NULL,
  `diagnose` varchar(500) COLLATE utf8mb4_hungarian_ci NOT NULL,
  `medicines` varchar(1000) COLLATE utf8mb4_hungarian_ci NOT NULL,
  `treatments` varchar(1000) COLLATE utf8mb4_hungarian_ci NOT NULL,
  `date` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_hungarian_ci;

--
-- A tábla adatainak kiíratása `examine`
--

INSERT INTO `examine` (`id`, `socialinsurancecode`, `diagnose`, `medicines`, `treatments`, `date`) VALUES
(1, '123123123', 'Fáradékony', 'Vodka-bomba-kataflám', 'Alvás', '2008-10-10'),
(2, '123123123', 'Viszket', '', 'Vakarja meg', '2010-02-02'),
(3, '111111111', 'adf', 'adf', 'adf', '2020-05-08'),
(4, '213344888', 'Kicsi a súlya.', 'Vitaminok', 'Tápszer', '2020-05-11'),
(5, '198237465', 'Mammográfiai', 'nincs', 'Szűrővizsgálat', '2020-05-11'),
(6, '112334566', 'Vizes láb.', 'vízhajtó', 'fásli', '2020-05-11'),
(7, '124356987', 'Fagyos tél', 'Meleg tea', 'Napi 3x', '2020-05-11'),
(8, '111999555', 'Viszket a háta.', 'Bőr krém', 'fürdés után kenni kell vele.', '2020-02-12'),
(9, '111999555', 'Köhögés és orrfolyás', 'Algopyrin', 'napi 3x', '2020-04-02'),
(10, '122133144', 'Láb fájdalom', 'krém', 'Pihenés és fásli előtte krém kenése napi 2x', '2020-05-12'),
(11, '112223211', 'Nehéz légzés miatti megfázás.', 'délfkdféldskfédslk', '2x vagy 3x', '2020-05-13');

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `patients`
--

CREATE TABLE `patients` (
  `name` varchar(100) COLLATE utf8mb4_hungarian_ci NOT NULL,
  `birthdate` date NOT NULL,
  `socialinsurancecode` varchar(9) COLLATE utf8mb4_hungarian_ci NOT NULL,
  `id` int(11) NOT NULL,
  `gender` varchar(1) COLLATE utf8mb4_hungarian_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_hungarian_ci;

--
-- A tábla adatainak kiíratása `patients`
--

INSERT INTO `patients` (`name`, `birthdate`, `socialinsurancecode`, `id`, `gender`) VALUES
('Kiss x', '1955-07-04', '123123123', 1, 'M'),
('Lapos Y', '2026-05-04', '111111111', 2, 'M'),
('XY YX', '1987-02-03', '122133144', 3, 'M'),
('Nagy Kálmán', '1993-01-02', '123456789', 4, 'M'),
('Kiss Pista', '1983-10-10', '123987654', 5, 'F'),
('Kiss Elek', '2013-12-01', '167234598', 6, 'M');

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `screeningtests`
--

CREATE TABLE `screeningtests` (
  `id` int(11) NOT NULL,
  `socialinsurancecode` varchar(9) COLLATE utf8mb4_hungarian_ci NOT NULL,
  `date` date NOT NULL,
  `screeningtest` varchar(100) COLLATE utf8mb4_hungarian_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_hungarian_ci;

--
-- A tábla adatainak kiíratása `screeningtests`
--

INSERT INTO `screeningtests` (`id`, `socialinsurancecode`, `date`, `screeningtest`) VALUES
(1, '111111111', '2020-03-11', 'Általános vizsgálat'),
(2, '123123123', '2017-01-01', 'Tüdőszűrő vizsgálat'),
(3, '122133144', '2018-05-17', 'Prosztata viszgálat'),
(4, '198237465', '2020-03-11', 'Labor vizsgálat'),
(5, '122133144', '2020-03-11', 'Tüdőszűrő vizsgálat'),
(6, '167234598', '2020-03-11', 'Vér vizsgálat');

--
-- Indexek a kiírt táblákhoz
--

--
-- A tábla indexei `examine`
--
ALTER TABLE `examine`
  ADD PRIMARY KEY (`id`),
  ADD KEY `patient_fk` (`socialinsurancecode`);

--
-- A tábla indexei `patients`
--
ALTER TABLE `patients`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `socialinsurancecode` (`socialinsurancecode`);

--
-- A tábla indexei `screeningtests`
--
ALTER TABLE `screeningtests`
  ADD PRIMARY KEY (`id`),
  ADD KEY `socialinsurancecode` (`socialinsurancecode`);

--
-- A kiírt táblák AUTO_INCREMENT értéke
--

--
-- AUTO_INCREMENT a táblához `examine`
--
ALTER TABLE `examine`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=26;

--
-- AUTO_INCREMENT a táblához `patients`
--
ALTER TABLE `patients`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT a táblához `screeningtests`
--
ALTER TABLE `screeningtests`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Megkötések a kiírt táblákhoz
--

--
-- Megkötések a táblához `examine`
--
ALTER TABLE `examine`
  ADD CONSTRAINT `patient_fk` FOREIGN KEY (`socialinsurancecode`) REFERENCES `patients` (`socialinsurancecode`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Megkötések a táblához `screeningtests`
--
ALTER TABLE `screeningtests`
  ADD CONSTRAINT `fk_patient` FOREIGN KEY (`socialinsurancecode`) REFERENCES `patients` (`socialinsurancecode`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
